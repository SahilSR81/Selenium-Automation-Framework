package com.saf.listeners;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.saf.base.BaseTest;
import com.saf.utils.ConfigReader;
import com.saf.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import java.io.File;
public class TestListener implements ITestListener {
private static ExtentReports extent;
// ThreadLocal so each parallel thread has its own ExtentTest node
private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

// Static block to initialize ExtentReports once when the class is loaded
static {
    new File("reports").mkdirs();
    ExtentSparkReporter spark =
        new ExtentSparkReporter("reports/ExtentReport.html");
    spark.config().setDocumentTitle("SAF Test Execution Report");
    spark.config().setReportName("Selenium Automation Framework — DemoWebShop");
    spark.config().setTheme(Theme.STANDARD);
    extent = new ExtentReports();
    extent.attachReporter(spark);
    extent.setSystemInfo("Project",    "selenium.automation.framework");
    extent.setSystemInfo("Author",     "Sahil Singh");
    extent.setSystemInfo("Base URL",   ConfigReader.get("BASE_URL"));
    extent.setSystemInfo("Framework",  "Selenium 4.18 + TestNG 7.9 + Maven");
    System.out.println("[TestListener] ExtentReports initialized");
}

// Test start
@Override
public void onTestStart(ITestResult result) {
    String testName = result.getTestClass().getName()
        + " :: " + result.getName();
    ExtentTest test = extent.createTest(testName);
    testNode.set(test);
    System.out.println("[TestListener] STARTED: " + testName);
}

// Test success
@Override
public void onTestSuccess(ITestResult result) {
    testNode.get().pass("✅ Test Passed");
    System.out.println("[TestListener] PASSED: " + result.getName());
}

// Test failure
@Override
public void onTestFailure(ITestResult result) {
    System.out.println("[TestListener] FAILED: " + result.getName());
    testNode.get().fail(result.getThrowable());

    // Get driver — try context first, then static accessor
    WebDriver driver = null;
    try {
        driver = (WebDriver) result.getTestContext().getAttribute("driver");
    } catch (Exception ignored) {}
    if (driver == null) driver = BaseTest.getDriver();

    // Screenshot utility listener
    if (driver != null) {
        String path = ScreenshotUtil.captureScreenshot(driver, result.getName());
        if (!path.isEmpty()) {
            try {
                testNode.get().addScreenCaptureFromPath(path, "Failure Screenshot");
                System.out.println("[TestListener] Screenshot attached: " + path);
            } catch (Exception e) {
                System.err.println("[TestListener] Screenshot attach error: "
                    + e.getMessage());
            }
        }
    } else {
        System.err.println("[TestListener] Driver null — cannot capture screenshot");
    }
}

// Skip test
@Override
public void onTestSkipped(ITestResult result) {
    testNode.get().skip("⏭ Skipped: " + result.getName());
    System.out.println("[TestListener] SKIPPED: " + result.getName());
}

// Finish
@Override
public void onFinish(ITestContext context) {
    if (extent != null) {
        extent.flush();
        System.out.println("[TestListener] Report saved -> reports/ExtentReport.html");
    }
}
}
