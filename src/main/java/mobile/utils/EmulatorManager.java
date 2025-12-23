
   package mobile.utils;

import java.io.*;
import java.util.*;

    public class EmulatorManager {

        private final String sdkPath;
        private final String emulatorPath;
        private final String adbPath;
        private final String avdName;

        // -------------------------------------------------------
        // CONSTRUCTOR (same logic as Python)
        // -------------------------------------------------------
        public EmulatorManager(String sdkPath, String avdName) {

            this.sdkPath = sdkPath != null
                    ? sdkPath
                    : System.getenv("ANDROID_HOME") != null
                    ? System.getenv("ANDROID_HOME")
                    : System.getenv("ANDROID_SDK_ROOT");

            if (this.sdkPath == null) {
                throw new RuntimeException("ANDROID_HOME or ANDROID_SDK_ROOT not set");
            }

            this.emulatorPath = this.sdkPath + "/emulator/emulator";
            this.adbPath = this.sdkPath + "/platform-tools/adb";

            // SAME AS PYTHON:
            // avd_name or get_default_avd()
            this.avdName = (avdName == null || avdName.isEmpty())
                    ? getDefaultAvd()
                    : avdName;
        }

        // -------------------------------------------------------
        // INTERNAL COMMANDS
        // -------------------------------------------------------
        private void runAsync(List<String> command) {
            try {
                new ProcessBuilder(command)
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private String run(List<String> command) {
            try {
                Process process = new ProcessBuilder(command).start();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));

                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                return output.toString();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // -------------------------------------------------------
        // AVD Detection (KEY PART YOU WERE MISSING)
        // -------------------------------------------------------
        private String getDefaultAvd() {

            List<String> cmd = List.of(emulatorPath, "-list-avds");
            String output = run(cmd).trim();

            if (output.isEmpty()) {
                throw new RuntimeException("❌ No AVD found locally. Please create one.");
            }

            String avd = output.split("\n")[0];
            System.out.println("✔ Local AVD detected: " + avd);
            return avd;
        }

        // -------------------------------------------------------
        // Start emulator (local only)
        // -------------------------------------------------------
        public void startEmulator() {
            System.out.println("🚀 Starting emulator: " + avdName);

            List<String> cmd = List.of(
                    emulatorPath,
                    "-avd", avdName,
                    "-no-cache",
                    "-no-boot-anim"
            );

            runAsync(cmd);

            try {
                Thread.sleep(10_000);
            } catch (InterruptedException ignored) {}
        }

        // -------------------------------------------------------
        // Wait for boot
        // -------------------------------------------------------
        public void waitForBoot(int timeoutSeconds) {

            System.out.println("⏳ Waiting for emulator to boot...");
            long start = System.currentTimeMillis();

            while ((System.currentTimeMillis() - start) / 1000 < timeoutSeconds) {

                String boot =
                        run(List.of(adbPath, "shell", "getprop", "sys.boot_completed"))
                                .trim();

                if ("1".equals(boot)) {
                    long elapsed = (System.currentTimeMillis() - start) / 1000;
                    System.out.println("✅ Boot completed in " + elapsed + "s");
                    return;
                }

                try {
                    Thread.sleep(5_000);
                } catch (InterruptedException ignored) {}
            }

            throw new RuntimeException("❌ Emulator failed to boot in time.");
        }

        // -------------------------------------------------------
        // Kill emulator
        // -------------------------------------------------------
        public void killEmulator() {
            System.out.println("🛑 Killing emulator...");
            run(List.of(adbPath, "emu", "kill"));
            System.out.println("💀 Emulator stopped.");
        }
    }
