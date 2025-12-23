#!/usr/bin/env bash
set -e

echo "=== Emulator ready ==="
adb devices -l
adb shell getprop ro.build.version.release

sleep 10

echo "=== Start Appium ==="
mkdir -p logs
appium --log-timestamp --log ./logs/appium.log &
APPIUM_PID=$!
echo "Appium PID: $APPIUM_PID"

echo "=== Wait for Appium ==="
for i in {1..30}; do
  if curl -s http://127.0.0.1:4723/status >/dev/null; then
    echo "Appium is ready!"
    break
  fi
  sleep 2
done

if ! curl -s http://127.0.0.1:4723/status >/dev/null; then
  echo "ERROR: Appium did not start"
  cat logs/appium.log || true
  kill "$APPIUM_PID" || true
  exit 1
fi

echo "=== Run tests ==="
mvn test -Dsurefire.suiteXmlFiles=MobileAutomationSuite.xml
TEST_EXIT_CODE=$?

echo "=== Capture logs ==="
tail -100 logs/appium.log || true
adb logcat -d > logs/emulator-logcat.txt || true

echo "=== Stop Appium ==="
kill "$APPIUM_PID" || true

exit "$TEST_EXIT_CODE"