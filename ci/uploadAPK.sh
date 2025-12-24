echo "install APK on emulator 5554"
adb -s emulator-5554 install -t -g ./src/test/resources/Utils/General-Store.apk


echo "Verify  emulador 5554:"
adb -s emulator-5554 shell pm list packages

