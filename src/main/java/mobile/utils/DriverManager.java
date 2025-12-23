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

  public void appTearDownwithEmulator() throws IOException {
      getDriver().quit();
      threadLocalDriver.remove();
      service.stop();
      emulatorManager.killEmulator();

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



     UiAutomator2Options buildOptions(){
        UiAutomator2Options   options = new UiAutomator2Options();
        //   WebDriverManager.chromedriver().setup();
       // String chromedriverPath = "C:\\Program Files\\Google\\Chrome\\Application\\chromedriver.exe";
        //     options.setChromedriverExecutable(chromedriverPath); // Set custom Chromedriver path
        //options.setCapability("chromedriverAutodownload", true);
         options.setApp(appPath);
         options.setAndroidInstallTimeout(Duration.ofMinutes(3));
         options.setUiautomator2ServerInstallTimeout(Duration.ofMinutes(3));
         options.setUiautomator2ServerLaunchTimeout(Duration.ofMinutes(3));
         options.setDisableWindowAnimation(true);
         options.setIgnoreHiddenApiPolicyError(true);
       // options.setAppPackage("com.androidsample.generalstore");
        //options.setAppActivity("com.androidsample.generalstore.MainActivity");
        // options.chromedriverUseSystemExecutable();

        return options;
    }

    AppiumDriverLocalService appiumServerService(int portNumber) {

        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(portNumber);

        return AppiumDriverLocalService.buildService(builder);
    }


}
