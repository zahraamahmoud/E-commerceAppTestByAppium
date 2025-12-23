package mobileTests;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.DeviceRotation;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import mobile.utils.DriverManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;

public class BaseTest {

    AndroidDriver driver;
    WebDriverWait explicitWait;
    DriverManager driverManager;
    String emuName="P7";
    int portNo=4724;

    @BeforeClass
    public void setup() throws IOException, InterruptedException, URISyntaxException {
        driverManager = new DriverManager();
        driver= driverManager.appSetupwithEmulator(System.getProperty("deviceName", emuName),portNo);
        explicitWait=new WebDriverWait(driver, Duration.ofSeconds(4));

    }

   @AfterClass
    public void tearDown() throws  IOException {
       driverManager.appTearDownwithEmulator();

    }



    public void dragAndDrop(WebElement element, int endX, int endY){
        ((JavascriptExecutor) driver).executeScript("mobile: dragGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) element).getId(),
                "endX", endX,
                "endY", endY
        ));
    }

    public void longPress(WebElement element){
        ((JavascriptExecutor) driver).executeScript("mobile: longClickGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) element).getId(),
                "duration",1000

        ));
    }
    public void scrollToTheEnd() throws InterruptedException {


        boolean canScrollMore;
        do {
            canScrollMore = (Boolean) ((JavascriptExecutor) driver).executeScript("mobile: scrollGesture", ImmutableMap.of("left", 100,
                    "top", 200, "width", 200, "height", 200, "direction", "down", "percent", 4.0));
        }while(canScrollMore );

    }

    public void swipeTo(WebElement element,String direction){
        ((JavascriptExecutor)driver).executeScript("mobile: swipeGesture",ImmutableMap.of("elementId",((RemoteWebElement)element).getId(),
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
