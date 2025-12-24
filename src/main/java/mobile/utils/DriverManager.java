package mobile.utils;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

import static mobile.utils.GetPaths.appPath;
import static mobile.utils.GetPaths.appiumJSPath;

public class DriverManager {


    private static final ThreadLocal<AndroidDriver> threadLocalDriver = new ThreadLocal<>();
    AppiumDriverLocalService service;

    EmulatorManager emulatorManager;
    public  DriverManager() throws IOException {
        emulatorManager=new EmulatorManager(null ,null);
    }


    public AndroidDriver getDriver() {
        return threadLocalDriver.get();
    }

    public void setDriver(AndroidDriver driver) {
        threadLocalDriver.set(driver);
    }

    public AndroidDriver appSetupwithEmulator(int PortNo) throws IOException, InterruptedException {

        emulatorManager.startEmulator();
        service= appiumServerService(PortNo);
        service.start();
        setDriver(initializeDriver(buildOptions(),PortNo));
        return getDriver();
    }
    public AndroidDriver appSetupRemotly(int PortNo) throws IOException, InterruptedException {

        setDriver(initializeDriver(buildRemoteOptions(),PortNo));
        return getDriver();
    }

  public void appTearDownwithEmulator() throws IOException {
      getDriver().quit();
      threadLocalDriver.remove();
      service.stop();
      emulatorManager.killEmulator();

  }
    public void appTearDownRemotly()  {
        getDriver().quit();
        threadLocalDriver.remove();

    }

     AndroidDriver initializeDriver(UiAutomator2Options options, int portNumber) {
        String serverUrl="http://127.0.0.1:"+portNumber;
         System.out.println(serverUrl);


        try {
            return new AndroidDriver(new URI(serverUrl).toURL(), options);
        }  catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + serverUrl, e);
        }
    }

    UiAutomator2Options buildRemoteOptions(){
        UiAutomator2Options   options = new UiAutomator2Options();
        //   WebDriverManager.chromedriver().setup();
        // String chromedriverPath = "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe";
        //     options.setChromedriverExecutable(chromedriverPath); // Set custom Chromedriver path
        //options.setCapability("chromedriverAutodownload", true);
        // options.setApp(appPath);
        options.setNewCommandTimeout(Duration.ofSeconds(600));
        options.setAdbExecTimeout(Duration.ofMillis(300000));
        options.setAndroidInstallTimeout(Duration.ofMillis(300000));
        options.setUiautomator2ServerInstallTimeout(Duration.ofMillis(300000));
        options.setUiautomator2ServerLaunchTimeout(Duration.ofMillis(300000));
        options.setUiautomator2ServerReadTimeout(Duration.ofMillis(300000));

        // Stability options
        options.setIgnoreHiddenApiPolicyError(true);
        options.setDisableWindowAnimation(true);
        options.setSkipServerInstallation(false);
        options.setSkipDeviceInitialization(false);
        options.setAppPackage("com.androidsample.generalstore");
        options.setAppActivity("com.androidsample.generalstore.MainActivity");
        // options.chromedriverUseSystemExecutable();

        return options;
    }

     UiAutomator2Options buildOptions(){
        UiAutomator2Options   options = new UiAutomator2Options();
        //   WebDriverManager.chromedriver().setup();
       // String chromedriverPath = "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe";
        //     options.setChromedriverExecutable(chromedriverPath); // Set custom Chromedriver path
        //options.setCapability("chromedriverAutodownload", true);
          options.setApp(appPath);
         options.setDisableWindowAnimation(true);
         options.setAppPackage("com.androidsample.generalstore");
         options.setAppActivity("com.androidsample.generalstore.MainActivity");
        // options.chromedriverUseSystemExecutable();

        return options;
    }

    AppiumDriverLocalService appiumServerService(int portNumber) {

        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(portNumber);

        return AppiumDriverLocalService.buildService(builder);
    }

    public void installAppiumSettingsIfNeeded(String deviceId) throws IOException, InterruptedException {
        // تحقق إذا الـ APK مثبت
        Process checkPackage = Runtime.getRuntime().exec(
                "adb -s " + deviceId + " shell dumpsys package io.appium.settings"
        );
        int exitCode = checkPackage.waitFor();

        if (exitCode != 0) {
            System.out.println("Appium settings APK not found. Installing...");
            // تثبيت الـ APK
            Process installAPK = Runtime.getRuntime().exec(
                    "adb -s " + deviceId + " install -g " +
                            System.getProperty("user.home") + "/.appium/node_modules/appium-uiautomator2-driver/node_modules/io.appium.settings/apks/settings_apk-debug.apk"
            );
            installAPK.waitFor();
            System.out.println("Appium settings APK installed successfully!");
        } else {
            System.out.println("Appium settings APK already installed.");
        }
    }
}
