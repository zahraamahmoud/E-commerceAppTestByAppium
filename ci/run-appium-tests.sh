#!/usr/bin/env bash
set -e

echo "=== Emulator ready ==="
adb devices -l
adb shell getprop ro.build.version.release

echo "=== Wait for emulator to be fully ready ==="
adb wait-for-device
sleep 30

# Ensure emulator is fully booted
BOOT_TIMEOUT=300
ELAPSED=0
until adb shell getprop sys.boot_completed 2>/dev/null | grep -q 1; do
  if [ $ELAPSED -ge $BOOT_TIMEOUT ]; then
    echo "ERROR: Emulator boot timeout after ${BOOT_TIMEOUT}s"
    exit 1
  fi
  echo "Waiting for emulator to finish booting... (${ELAPSED}s)"
  sleep 10
  ELAPSED=$((ELAPSED + 10))
done
echo "Emulator boot completed!"

# Wait for package manager to be ready
echo "Waiting for package manager..."
until adb shell pm list packages >/dev/null 2>&1; do
  echo "Package manager not ready yet..."
  sleep 5
done
echo "Package manager ready!"

# Give extra time for system services
sleep 20

# Verify emulator is stable
echo "Verifying emulator stability..."
adb devices
adb shell getprop ro.build.version.sdk

echo "=== Start Appium with increased timeouts ==="
mkdir -p logs

# Kill any existing Appium processes
pkill -f appium || true
sleep 2

# Start Appium with simplified configuration
# Note: Multi-line JSON in bash can be tricky, using inline format instead
appium \
  --log-timestamp \
  --log ./logs/appium.log \
  --relaxed-security \
  --session-override \
  --allow-insecure chromedriver_autodownload \
  --default-capabilities '{"appium:newCommandTimeout":600,"appium:adbExecTimeout":300000,"appium:androidInstallTimeout":300000,"appium:uiautomator2ServerInstallTimeout":300000,"appium:uiautomator2ServerLaunchTimeout":300000}' &

APPIUM_PID=$!
echo "Appium PID: $APPIUM_PID"

# Verify Appium process is running
sleep 5
if ! ps -p $APPIUM_PID > /dev/null 2>&1; then
  echo "ERROR: Appium process died immediately after starting"
  echo "Last 50 lines of Appium log:"
  tail -50 logs/appium.log || echo "No log file found"
  exit 1
fi

echo "=== Wait for Appium ==="
APPIUM_READY=false
for i in {1..60}; do
  if curl -s http://127.0.0.1:4723/status >/dev/null 2>&1; then
    echo "Appium is ready!"
    APPIUM_READY=true
    break
  fi
  echo "Attempt $i/60: Waiting for Appium..."
  sleep 2
  
  # Check if Appium process is still running
  if ! ps -p $APPIUM_PID > /dev/null 2>&1; then
    echo "ERROR: Appium process died while waiting for it to be ready"
    echo "Last 50 lines of Appium log:"
    tail -50 logs/appium.log || echo "No log file found"
    exit 1
  fi
done

if [ "$APPIUM_READY" = false ]; then
  echo "ERROR: Appium did not start within 120 seconds"
  echo "Appium process status:"
  ps -p $APPIUM_PID || echo "Process not found"
  echo "Last 100 lines of Appium log:"
  tail -100 logs/appium.log || echo "No log file found"
  kill "$APPIUM_PID" 2>/dev/null || true
  exit 1
fi

echo "=== Verify Appium capabilities ==="
curl -s http://127.0.0.1:4723/status | jq . || true

echo "=== Run tests ==="
mvn test -Dsurefire.suiteXmlFiles=MobileAutomationSuite.xml -DRUN_MODE=remote
TEST_EXIT_CODE=$?

echo "=== Capture logs ==="
echo "Last 100 lines of Appium log:"
tail -100 logs/appium.log || true
echo "Capturing emulator logcat..."
adb logcat -d > logs/emulator-logcat.txt || true

echo "=== Stop Appium ==="
kill "$APPIUM_PID" || true
wait "$APPIUM_PID" 2>/dev/null || true

if [ $TEST_EXIT_CODE -eq 0 ]; then
  echo "=== Tests PASSED ==="
else
  echo "=== Tests FAILED with exit code $TEST_EXIT_CODE ==="
fi

exit "$TEST_EXIT_CODE"