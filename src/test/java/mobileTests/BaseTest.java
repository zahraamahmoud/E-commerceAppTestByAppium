package mobileTests;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.DeviceRotation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import mobile.utils.DriverManager;

import java.io.IOException;
import java.time.Duration;

public class BaseTest {

    AndroidDriver driver;
    WebDriverWait explicitWait;
    DriverManager driverManager;
    String emuName="P7";
    int portNo=4723;
    public String runMode = System.getProperty("RUN_MODE", "local");

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        driverManager = new DriverManager();

        if (runMode.equalsIgnoreCase("remote")) {
            driverManager.installAppiumSettingsIfNeeded("emulator-5554");
            driver = driverManager.appSetupRemotly(portNo);
        } else {
            driver = driverManager.appSetupwithEmulator(portNo);
        }

        explicitWait = new WebDriverWait(driver, Duration.ofSeconds(4));
    }

   @AfterClass
    public void tearDown() throws  IOException {
       if (runMode.equalsIgnoreCase("remote")) {
              driverManager.appTearDownRemotly();
       } else {
               driverManager.appTearDownwithEmulator();
       }
    }



    public void dragAndDrop(WebElement element, int endX, int endY){
        driver.executeScript("mobile: dragGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) element).getId(),
                "endX", endX,
                "endY", endY
        ));
    }

    public void longPress(WebElement element){
        driver.executeScript("mobile: longClickGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) element).getId(),
                "duration",1000

        ));
    }
    public void scrollToTheEnd() throws InterruptedException {


        boolean canScrollMore;
        do {
            canScrollMore = (Boolean) driver.executeScript("mobile: scrollGesture", ImmutableMap.of("left", 100,
                    "top", 200, "width", 200, "height", 200, "direction", "down", "percent", 4.0));
        }while(canScrollMore );

    }

    public void swipeTo(WebElement element,String direction){
        driver.executeScript("mobile: swipeGesture",ImmutableMap.of("elementId",((RemoteWebElement)element).getId(),
                "direction",direction,
                "percent",.1));
    }
    public void scrollToElement(String elementText){

        driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+elementText+"\"));"));

    }

    public void rotateToLandscape(){

        DeviceRotation landscape=new DeviceRotation(0,0,90);
        driver.rotate(landscape);

    }

}
