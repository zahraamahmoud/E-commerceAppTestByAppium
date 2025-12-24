echo "Instalando APK no emulador 5554..."
adb -s emulator-5554 install -t -g ./src/test/resources/Utils/General-Store.apk


echo "Verificando pacotes no emulador 5554:"
adb -s emulator-5554 shell pm list packages | grep calculator || echo "Pacote não encontrado no emulador 5554"

