package mobile.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.Properties;

public class GetPaths {
    public static String adbPath;
    public static String emulatorPath;

    public static String  appiumJSPath;
    public static String appPath;

    static {
        try {
            loadPathsFromConfig();
        } catch (IOException e) {
            System.err.println("Error loading paths: " + e.getMessage());
        }
    }
    private static void loadPathsFromConfig() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(System.getProperty("user.dir")+"/config.properties")) {
            properties.load(input);
            String user = System.getProperty("user.name");
            adbPath = properties.getProperty("adbPath").replace("<username>", user);
            emulatorPath = properties.getProperty("emulatorPath").replace("<username>", user);
            appiumJSPath = properties.getProperty("appiumJSPath").replace("<username>", user);
            appPath=properties.getProperty("appPath").replace("<userdir>", System.getProperty("user.dir"));
        }
    }
}