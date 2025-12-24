#!/usr/bin/env bash
set -e

echo "=== Emulator ready ==="
adb devices -l
adb shell getprop ro.build.version.release

echo "=== Wait for emulator to be fully ready ==="
adb wait-for-device
sleep 30

# Ensure emulator is fully booted
until adb shell getprop sys.boot_completed | grep -q 1; do
  echo "Waiting for emulator to finish booting..."
  sleep 5
done
echo "Emulator boot completed!"

# Give extra time for system services
sleep 15

echo "=== Start Appium with increased timeouts ==="
mkdir -p logs
appium --log-timestamp \
  --log ./logs/appium.log \
  --relaxed-security \
  --default-capabilities '{
    "appium:newCommandTimeout": 300,
    "appium:adbExecTimeout": 300000,
    "appium:androidInstallTimeout": 300000,
    "appium:uiautomator2ServerInstallTimeout": 300000,
    "appium:uiautomator2ServerLaunchTimeout": 300000
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