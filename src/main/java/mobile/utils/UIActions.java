package mobile.utils;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.DeviceRotation;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class UIActions {
    DriverManager driverManager;
    AndroidDriver driver;
    WebDriverWait explicitWait;

    public  UIActions () throws IOException {
        driverManager = new DriverManager();
        this.driver=driverManager.getDriver();
        explicitWait=new WebDriverWait(driver, Duration.ofSeconds(10));

    }


    public void click(By element){

        waitForElementToBeClickable(element).click();
    }
    public void click(AppiumBy element){
        waitForElementToBeClickable(element).click();

    }

    public String getElementText(By element){
       return
               waitForElementVisibility(element).getText();


    }
    public String getElementText(AppiumBy element){
        return
                waitForElementVisibility(element).getText();



    }
    public void setElementText(By element,String text){
        waitForElementVisibility(element).sendKeys(text);

    }
    public void setElementText(AppiumBy element ,String text){
        waitForElementVisibility(element).sendKeys(text);

    }
    public String getAttribute(By element,String attribute){

        return driver.findElement(element).getDomAttribute(attribute);
    }
    public String getAttribute(AppiumBy element,String attribute){

        return driver.findElement(element).getDomAttribute(attribute);
    }


    protected void dragAndDrop(WebElement element, int endX, int endY){


        executeMobileGesture("mobile: dragGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) element).getId(),
                "endX", endX,
                "endY", endY
        ));

    }

    protected void longPress(WebElement element){
        executeMobileGesture("mobile: longClickGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) element).getId(),
                "duration",1000

        ));

    }
    protected void scrollToTheEnd(){


        boolean canScrollMore;
        do {            canScrollMore = (Boolean) executeMobileGesture("mobile: scrollGesture", ImmutableMap.of("left", 100,

                    "top", 200, "width", 200, "height", 200, "direction", "down", "percent", 4.0));
        }while(canScrollMore );

    }

    protected void swipeTo(WebElement element,String direction){
        executeMobileGesture("mobile: swipeGesture",ImmutableMap.of("elementId",((RemoteWebElement)element).getId(),
                "direction",direction,
                "percent",.1));

    }
    public void scrollToElement(String elementText){

        driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+elementText+"\"));"));

    }

    protected void rotateToLandscape(){

        DeviceRotation landscape=new DeviceRotation(0,0,90);
        driver.rotate(landscape);

    }


    private WebElement waitForElementToBeClickable(By locator) {
        return explicitWait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement waitForElementToBeClickable(AppiumBy locator) {
        return explicitWait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement waitForElementVisibility(By locator) {
        return explicitWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitForElementVisibility(AppiumBy locator) {
        return explicitWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    private Object executeMobileGesture(String script, Map<String, Object> args) {
        return driver.executeScript(script, args);
    }
}
