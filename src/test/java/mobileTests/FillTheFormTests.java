package mobileTests;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class FillTheFormTests extends BaseTest {

String country="Egypt";
String name="zahraa";
String gender="Female";
String productName=" 4 Retro";
String productName2="PG 3";
Double pricesum=0.0;
    @Test(priority = 1)
    public void fillTheFormTest(){

    driver.findElement(By.id("com.androidsample.generalstore:id/spinnerCountry")).click();
    scrollToElement(country);
    driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\""+country+"\")")).click();
    assertEquals(driver.findElement(By.xpath("//android.widget.TextView[@resource-id=\"android:id/text1\"]")).getText(),country);

    WebElement namefield= driver.findElement(By.id("com.androidsample.generalstore:id/nameField"));
    namefield.sendKeys(name);
    assertEquals(namefield.getText(),name);
    WebElement genderradio =driver.findElement(By.xpath("//android.widget.RadioButton[@text=\""+gender+"\"]"));
    genderradio.click();
    assertEquals(genderradio.getDomAttribute("checked"),"true");
    driver.findElement(By.id("com.androidsample.generalstore:id/btnLetsShop")).click();
    WebElement pageTitle =explicitWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.androidsample.generalstore:id/toolbar_title")));
    assertEquals(pageTitle.getText(),"Products");

    }


    @Test
    public void validateNameFieldIsMandatory(){

        driver.findElement(By.id("com.androidsample.generalstore:id/spinnerCountry")).click();
        scrollToElement(country);
        driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().text(\""+country+"\")")).click();
        assertEquals(driver.findElement(By.xpath("//android.widget.TextView[@resource-id=\"android:id/text1\"]")).getText(),country);
        driver.findElement(By.id("com.androidsample.generalstore:id/btnLetsShop")).click();
        String errorMessage=explicitWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//android.widget.Toast)[1]"))).getText();
        assertEquals(errorMessage,"Please enter your name");
    }
    @Test(priority = 2)
   public  void addItemToCartTest(){
        driver.executeScript("mobile: startActivity",
                ImmutableMap.of("intent","com.androidsample.generalstore/com.androidsample.generalstore.AllProductsActivity"));
        WebElement addToCartbtn=driver.findElement(By.xpath("//android.widget.TextView[contains(@text,'"+productName+"')]/following-sibling::android.widget.LinearLayout/android.widget.TextView[@resource-id='com.androidsample.generalstore:id/productAddCart']"));
       addToCartbtn.click();
       assertEquals(addToCartbtn.getText(),"ADDED TO CART");

}
      @Test
    public  void addTwoItemsToCartTest() throws InterruptedException {
        driver.executeScript("mobile: startActivity",
                ImmutableMap.of("intent","com.androidsample.generalstore/com.androidsample.generalstore.AllProductsActivity"));
        WebElement addToCartbtn=driver.findElement(By.xpath("//android.widget.TextView[contains(@text,'"+productName+"')]/following-sibling::android.widget.LinearLayout/android.widget.TextView[@resource-id='com.androidsample.generalstore:id/productAddCart']"));
        addToCartbtn.click();
        assertEquals(addToCartbtn.getText(),"ADDED TO CART");
        PointerInput touchInput = new PointerInput(PointerInput.Kind.TOUCH, "default mouse");
          Sequence touchAction = new Sequence(touchInput, 0);

// First action: Move to an absolute position (500, 1000)
          touchAction.addAction(touchInput.createPointerMove(Duration.ofMillis(200),
                  PointerInput.Origin.viewport(), 500, 1000));

// Second action: Pointer down
          touchAction.addAction(touchInput.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));

// Third action: Move relative to the current position
          touchAction.addAction(touchInput.createPointerMove(Duration.ofMillis(200),
                  PointerInput.Origin.pointer(), 0, -800));

// Fourth action: Pointer up
          touchAction.addAction(touchInput.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

// Perform the action
          driver.perform(List.of(touchAction));



    /*      scrollToElement(productName2);
        WebElement addToCart2btn=driver.findElement(By.xpath("//android.widget.TextView[contains(@text,'"+productName2+"')]/following-sibling::android.widget.LinearLayout/android.widget.TextView[@resource-id='com.androidsample.generalstore:id/productAddCart']"));
        addToCart2btn.click();
        assertEquals(addToCart2btn.getText(),"ADDED TO CART");
        driver.findElement(By.id("com.androidsample.generalstore:id/appbar_btn_cart")).click();
       // Thread.sleep(3000);
        explicitWait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("com.androidsample.generalstore:id/toolbar_title"),"Cart"));
        List<WebElement> priceList=driver.findElements(By.xpath("//android.widget.TextView[@resource-id=\"com.androidsample.generalstore:id/productPrice\"]"));


        for(WebElement price:priceList){

         pricesum+=Double.parseDouble(price.getText().replace("$", ""));

        }

       Double totalPrice=Double.parseDouble(driver.findElement(By.xpath("//android.widget.TextView[@resource-id='com.androidsample.generalstore:id/totalAmountLbl']")).getText().replace("$", ""));

        assertEquals(totalPrice,pricesum);
        longPress(driver.findElement(By.id("com.androidsample.generalstore:id/termsButton")));
        assertTrue(driver.findElement(By.id("com.androidsample.generalstore:id/alertTitle")).isDisplayed());
        driver.findElement(By.id("android:id/button1")).click();
       //     WebElement sendMeEmail= driver.findElement(By.className("android.widget.CheckBox"));
     //   sendMeEmail.click();
       // assertEquals(sendMeEmail.getDomAttribute("checked"),"true");
          explicitWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.androidsample.generalstore:id/btnProceed"))).click();
          Thread.sleep(5000);
          Set<String> contexts= driver.getContextHandles();
         // System.out.println(contexts);

       driver.context("WEBVIEW_com.androidsample.generalstore");
       driver.findElement(By.name("q")).sendKeys("appium");
       driver.findElement(By.name("q")).sendKeys(Keys.ENTER);


       driver.pressKey(new KeyEvent(AndroidKey.BACK));
       driver.context("NATIVE_APP");
*/
      }

}
