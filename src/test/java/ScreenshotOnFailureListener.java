import io.appium.java_client.android.AndroidDriver;
import mobile.utils.DriverManager;
import mobile.utils.ScreenShot;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.IOException;

public class ScreenshotOnFailureListener implements ITestListener {


    DriverManager driverManager;


    @Override
    public void onTestFailure(ITestResult result) {
        try {
            driverManager=new DriverManager();
        } catch (IOException e) {
//            throw new RuntimeException(e);
        }
        AndroidDriver driver = driverManager.getDriver();
        String testName = result.getName();
        String screenshotPath=ScreenShot.captureScreenshot(driver, testName);
        System.out.println(screenshotPath);
        if (screenshotPath != null) {

            String relativeScreenshotPath = screenshotPath.replace("test-output/", "");
            String imgTag = "<a href='./" + relativeScreenshotPath + "' target='_blank'><img src='" + relativeScreenshotPath + "' height='400' width='300'/></a>";
            Reporter.log("Screenshot for failed test: <br>" + imgTag);
        }
    }




}
