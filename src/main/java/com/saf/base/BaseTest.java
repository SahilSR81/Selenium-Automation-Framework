package com.saf.base;
import com.saf.utils.ConfigReader;
import com.saf.utils.ExcelUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.ITestContext;
import org.testng.annotations.*;
import java.time.Duration;
import java.util.logging.Level;

public class BaseTest {
// ThreadLocal: each parallel thread has its own WebDriver instance
private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

// Protected reference for subclass access
protected WebDriver driver;

@Parameters({"browser"})
@BeforeClass(alwaysRun = true)
public void setUp(@Optional("chrome") String browserParam,
                  ITestContext context) {

    // Priority: -Dbrowser JVM arg > testng.xml param > default chrome
    String browserName = System.getProperty("browser");
    if (browserName == null || browserName.trim().isEmpty()) {
        browserName = browserParam;
    }
    if (browserName == null || browserName.trim().isEmpty()) {
        browserName = ConfigReader.get("BROWSER");
    }
    if (browserName == null || browserName.trim().isEmpty()) {
        browserName = "chrome";
    }
    browserName = browserName.trim().toLowerCase();
    System.out.println("[BaseTest] Launching browser: " + browserName);

    WebDriver webDriver;

    // Different browser setups
    switch (browserName) {
        //Firefox brrowser setup
        case "firefox":
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions ffOpts = new FirefoxOptions();
            ffOpts.addArguments("--start-maximized");
            webDriver = new FirefoxDriver(ffOpts);
            System.out.println("[BaseTest] Firefox launched");
            break;
        
        // Edge browser setup
        case "edge":
            // using local msedgedriver because azureedge.net is blocked on this network
            EdgeOptions edgeOpts = new EdgeOptions();
            edgeOpts.addArguments("--start-maximized");
            edgeOpts.addArguments("--disable-notifications");
            edgeOpts.addArguments("--disable-blink-features=AutomationControlled");

            // resolve driver path relative to project root — works on any machine
            String driverPath = System.getProperty("user.dir")
                + "/src/test/resources/drivers/msedgedriver.exe";
            java.io.File driverFile = new java.io.File(driverPath);

            if (driverFile.exists()) {
                // found in local driver — set it directly, skip network lookup
                System.setProperty("webdriver.edge.driver", driverPath);
                System.out.println("[BaseTest] edge driver loaded from project: " + driverPath);
            } else {
                // local driver missing — log clear instructions
                System.out.println("[BaseTest] msedgedriver.exe not found at: " + driverPath);
                System.out.println("[BaseTest] download from: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/");
                System.out.println("[BaseTest] place it at: src/test/resources/drivers/msedgedriver.exe");
                throw new RuntimeException(
                    "msedgedriver.exe not found. Download Edge 145 driver from " +
                    "https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/ " +
                    "and place at: src/test/resources/drivers/msedgedriver.exe"
                );
            }

            webDriver = new EdgeDriver(edgeOpts);
            System.out.println("[BaseTest] edge launched successfully");
            break;

        default: // chrome
            WebDriverManager.chromedriver().setup();
            ChromeOptions chromeOpts = new ChromeOptions();
            chromeOpts.addArguments("--start-maximized");
            chromeOpts.addArguments("--disable-notifications");
            chromeOpts.addArguments("--disable-blink-features=AutomationControlled");
            chromeOpts.addArguments("--no-sandbox");
            chromeOpts.addArguments("--disable-dev-shm-usage");
            chromeOpts.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                + "AppleWebKit/537.36 (KHTML, like Gecko) "
                + "Chrome/120.0.0.0 Safari/537.36");
            chromeOpts.setExperimentalOption("excludeSwitches",
                new String[]{"enable-automation"});
            chromeOpts.setExperimentalOption("useAutomationExtension", false);
            // Enable browser console logs (Selenium 4 feature)
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            chromeOpts.setCapability("goog:loggingPrefs", logPrefs);
            webDriver = new ChromeDriver(chromeOpts);
            System.out.println("[BaseTest] Chrome launched");
            break;
    }

    // Remove webdriver property to reduce bot-detection flags
    ((JavascriptExecutor) webDriver).executeScript(
        "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

    // Global timeouts
    webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

    // Store in ThreadLocal — safe for parallel execution
    driverThread.set(webDriver);
    driver = driverThread.get();

    // Share with TestListener via ITestContext
    context.setAttribute("driver", driver);

    // Create test data file if missing
    ExcelUtil.createTestDataFileIfMissing(
        "src/test/resources/testdata.xlsx");

    // Navigate to base URL
    String url = ConfigReader.get("BASE_URL");
    driver.get(url);
    System.out.println("[BaseTest] Navigated to: " + url);
    System.out.println("[BaseTest] Page title: " + driver.getTitle());
}

// Sync driver reference before each test method to ensure it's always up-to-date
@BeforeMethod(alwaysRun = true)
public void syncDriver() {
    // Sync protected field before each test method
    driver = driverThread.get();
    System.out.println("[BaseTest] @BeforeMethod — driver synced");
}

// Optional @AfterMethod for logging or cleanup after each test method
@AfterMethod(alwaysRun = true)
public void afterMethod() {
    System.out.println("[BaseTest] @AfterMethod — test method complete");
}

// Clean up WebDriver after all tests in the class have run
@AfterClass(alwaysRun = true)
public void tearDown() {
    WebDriver wd = driverThread.get();
    if (wd != null) {
        wd.quit();
        driverThread.remove();
        driver = null;
        System.out.println("[BaseTest] Browser closed and driver removed");
    }
}

/* Static accessor for TestListener to retrieve driver on failure */
public static WebDriver getDriver() {
    return driverThread.get();
}
}
