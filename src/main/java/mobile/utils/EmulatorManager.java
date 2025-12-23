package mobile.utils;

import java.io.*;
import java.util.Properties;

import static mobile.utils.GetPaths.adbPath;
import static mobile.utils.GetPaths.emulatorPath;

public class EmulatorManager {




    public void startEmulator(String avdName) throws IOException, InterruptedException {

        ProcessBuilder process = new ProcessBuilder(emulatorPath, "-avd", avdName, "-no-boot-anim");
        process.start();

        Thread.sleep(7000);
    }

    public void stopEmulator() throws IOException {

        String command =  adbPath + " -s " + getRunningEmulatorId() + " emu kill";
        ProcessBuilder process = new ProcessBuilder(command.split(" "));
        process.start();
    }
    private String getRunningEmulatorId() throws IOException {
        String command = adbPath + " devices";
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("emulator-")) {
                return line.split("\t")[0];
            }
        }
        return null;
    }







}
