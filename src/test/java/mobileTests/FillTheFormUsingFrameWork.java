package mobileTests;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import mobile.pom.FormPage;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


@Listeners(ScreenshotOnFailureListener.class)

public class FillTheFormUsingFrameWork extends BaseTest {


    String country="Egypt";
    String name="zahraa";
    String gender="Female";
    String productName=" 4 Retro";
    String productName2="PG 3";
    FormPage formPage;
    Double pricesum=0.0;
    @Test
    public void fillTheFormTest() throws IOException {
        formPage=new FormPage();
        formPage.selectCountry(country);
        assertEquals(formPage.getSelectedCountry(),"Egypt");
        formPage.setName(name);
        assertEquals(formPage.getName(),name);

        assertEquals(formPage.selectGender(gender),"true");
        formPage.submitForm();

    }

}
