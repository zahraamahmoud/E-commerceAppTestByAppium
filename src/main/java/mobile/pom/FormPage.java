package mobile.pom;

import org.openqa.selenium.By;
import mobile.utils.UIActions;

import java.io.IOException;

public class FormPage {

    UIActions uiActions;


    By countryDropDown=By.id("com.androidsample.generalstore:id/spinnerCountry");

    By selectedCountryField=By.xpath("//android.widget.TextView[@resource-id=\"android:id/text1\"]");
    By nameField=By.id("com.androidsample.generalstore:id/nameField");
    By letsShopBTN=By.id("com.androidsample.generalstore:id/btnLetsShop");
    public  FormPage() throws IOException {
        uiActions=new UIActions();

    }


    public void selectCountry(String country){
        uiActions.click(countryDropDown);
        uiActions.scrollToElement(country);
        By countryLocator = By.xpath(String.format("//android.widget.TextView[@text='%s']", country));
        uiActions.click(countryLocator);
    }

    public String getSelectedCountry(){
     return uiActions.getElementText(selectedCountryField)  ;
    }
    public void setName(String text){
        uiActions.setElementText(nameField,text);
    }
    public String getName(){
       return  uiActions.getElementText(nameField);
    }

    public String selectGender(String gender){
       By genderLocator= By.xpath(String.format("//android.widget.RadioButton[@text='%s']",gender));

        uiActions.click(genderLocator);

        return uiActions.getAttribute(genderLocator,"checked");
    }

    public void submitForm(){

        uiActions.click(letsShopBTN);

    }
}
