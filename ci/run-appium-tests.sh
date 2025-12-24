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

appium --log-timestamp \
  --log ./logs/appium.log \
  --relaxed-security \
  --session-override \
  --default-capabilities '{
    "appium:newCommandTimeout": 600,
    "appium:adbExecTimeout": 300000,
    "appium:androidInstallTimeout": 300000,
    "appium:uiautomator2ServerInstallTimeout": 300000,
    "appium:uiautomator2ServerLaunchTimeout": 300000,
    "appium:uiautomator2ServerReadTimeout": 300000,
    "appium:skipServerInstallation": false,
    "appium:skipDeviceInitialization": false
  }' &
APPIUM_PID=$!
echo "Appium PID: $APPIUM_PID"

echo "=== Wait for Appium ==="
for i in {1..30}; do
  if curl -s http://127.0.0.1:4723/status >/dev/null; then
    echo "Appium is ready!"
    break
  fi
  echo "Attempt $i/30: Waiting for Appium..."
  sleep 2
done

if ! curl -s http://127.0.0.1:4723/status >/dev/null; then
  echo "ERROR: Appium did not start"
  cat logs/appium.log || true
  kill "$APPIUM_PID" || true
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