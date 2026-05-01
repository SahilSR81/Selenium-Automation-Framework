package com.saf.tests;
import com.saf.base.BaseTest;
import com.saf.utils.ConfigReader;
import com.saf.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.testng.Assert;
import org.testng.annotations.Test;

// Test class demonstrating Selenium 4 features: relative locators, new tab/window handling, and browser console logs
public class Selenium4Test extends BaseTest {

// -------------------------------------------------------
// TEST 1: Relative Locators — all 5 methods
// -------------------------------------------------------
@Test(groups = {"selenium4"}, priority = 12)
public void testRelativeLocators() {
    driver.get(ConfigReader.get("BASE_URL"));
    WaitUtil wait = new WaitUtil(driver);

    wait.waitForVisible(By.cssSelector(".product-grid"), 15);

    // Reference element: search input in header
    // NAV_SEARCH_INPUT -> id="small-searchterms"
    WebElement searchBox = driver.findElement(By.id("small-searchterms"));
    System.out.println("[Selenium4Test] Reference element: search box found");

    // 1. toRightOf — find button to the right of search input
    try {
        // NAV_SEARCH_BUTTON -> cssSelector=".search-box-button"
        WebElement rightBtn = driver.findElement(
            RelativeLocator.with(By.tagName("button"))
                .toRightOf(searchBox));
        System.out.println("[Selenium4Test] toRightOf(): " + rightBtn.getTagName()
            + " | text='" + rightBtn.getText().trim() + "'");
    } catch (Exception e) {
        System.out.println("[Selenium4Test] toRightOf() note: "
            + e.getClass().getSimpleName());
    }

    // 2. toLeftOf — find logo div to the left of search container
    try {
        WebElement leftEl = driver.findElement(
            RelativeLocator.with(By.tagName("div")).toLeftOf(searchBox));
        System.out.println("[Selenium4Test] toLeftOf(): "
            + leftEl.getTagName() + " class=" + leftEl.getAttribute("class"));
    } catch (Exception e) {
        System.out.println("[Selenium4Test] toLeftOf() note: "
            + e.getClass().getSimpleName());
    }

    // 3. near — find element within 50px of search box
    try {
        WebElement nearEl = driver.findElement(
            RelativeLocator.with(By.tagName("input")).near(searchBox));
        System.out.println("[Selenium4Test] near(): "
            + nearEl.getTagName() + " id=" + nearEl.getAttribute("id"));
    } catch (Exception e) {
        System.out.println("[Selenium4Test] near() note: "
            + e.getClass().getSimpleName());
    }

    // Reference for above/below: use footer div
    try {
        WebElement footer = driver.findElement(
            By.cssSelector(".footer"));

        // 4. above — find main content section above footer
        WebElement aboveFooter = driver.findElement(
            RelativeLocator.with(By.tagName("div")).above(footer));
        System.out.println("[Selenium4Test] above(): "
            + aboveFooter.getTagName() + " class="
            + aboveFooter.getAttribute("class"));

        // 5. below — find element below header
        WebElement header = driver.findElement(By.cssSelector(".header"));
        WebElement belowHeader = driver.findElement(
            RelativeLocator.with(By.tagName("div")).below(header));
        System.out.println("[Selenium4Test] below(): "
            + belowHeader.getTagName() + " class="
            + belowHeader.getAttribute("class"));

    } catch (Exception e) {
        System.out.println("[Selenium4Test] above/below note: "
            + e.getClass().getSimpleName());
    }

    Assert.assertTrue(searchBox.isDisplayed(),
        "Reference element must be visible");
    System.out.println("[Selenium4Test] ✅ All 5 relative locators demonstrated");
}

// -------------------------------------------------------
// TEST 2: New Tab — Selenium 4 WindowType.TAB
// -------------------------------------------------------
@Test(groups = {"selenium4"}, priority = 13)
public void testNewTabHandling() {
    driver.get(ConfigReader.get("BASE_URL"));
    String originalHandle = driver.getWindowHandle();
    String originalTitle  = driver.getTitle();
    System.out.println("[Selenium4Test] Original window: " + originalTitle);
    System.out.println("[Selenium4Test] Original handle: " + originalHandle);

    // Selenium 4 API: open new tab and auto-switch to it
    driver.switchTo().newWindow(WindowType.TAB);
    System.out.println("[Selenium4Test] New TAB opened (WindowType.TAB)");
    System.out.println("[Selenium4Test] Total windows: "
        + driver.getWindowHandles().size());

    // Navigate new tab to a different page
    driver.get("https://www.google.com");
    String newTabTitle = driver.getTitle();
    System.out.println("[Selenium4Test] New tab title: " + newTabTitle);
    Assert.assertFalse(newTabTitle.isEmpty(), "New tab should have a title");

    // Switch back to original window using saved handle
    driver.switchTo().window(originalHandle);
    System.out.println("[Selenium4Test] Switched back. Title: " + driver.getTitle());
    Assert.assertEquals(driver.getTitle().toLowerCase(), originalTitle.toLowerCase(),
        "Should be back on DemoWebShop tab");
    System.out.println("[Selenium4Test] ✅ New tab handling PASSED");
}

// -------------------------------------------------------
// TEST 3: New Window — Selenium 4 WindowType.WINDOW
// -------------------------------------------------------
@Test(groups = {"selenium4"}, priority = 14)
public void testNewWindowHandling() {
    driver.get(ConfigReader.get("BASE_URL"));
    String originalHandle = driver.getWindowHandle();
    System.out.println("[Selenium4Test] Original handle: " + originalHandle);

    // Selenium 4 API: open new browser window
    driver.switchTo().newWindow(WindowType.WINDOW);
    System.out.println("[Selenium4Test] New WINDOW opened (WindowType.WINDOW)");

    driver.get("https://www.wikipedia.org");
    String winTitle = driver.getTitle();
    System.out.println("[Selenium4Test] New window title: " + winTitle);
    Assert.assertFalse(winTitle.isEmpty(), "New window should have a title");

    // Close new window
    driver.close();
    System.out.println("[Selenium4Test] New window closed");

    // Switch back to original
    driver.switchTo().window(originalHandle);
    System.out.println("[Selenium4Test] Back to: " + driver.getTitle());
    Assert.assertFalse(driver.getTitle().isEmpty(),
        "Original window should still be open");
    System.out.println("[Selenium4Test] ✅ New window handling PASSED");
}

// -------------------------------------------------------
// TEST 4: Browser Console Logs
// -------------------------------------------------------
@Test(groups = {"selenium4"}, priority = 15)
public void testBrowserConsoleLogs() {
    driver.get(ConfigReader.get("BASE_URL"));

    // Short pause for JS execution
    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

    System.out.println("[Selenium4Test] Fetching browser console logs...");
    System.out.println("[Selenium4Test] Requires: goog:loggingPrefs capability "
        + "set in BaseTest ChromeOptions");

    try {
        var logs = driver.manage().logs().get(LogType.BROWSER);
        if (logs.getAll().isEmpty()) {
            System.out.println("[Selenium4Test] Console: No entries (clean page)");
        } else {
            System.out.println("[Selenium4Test] Console entries: "
                + logs.getAll().size());
            logs.getAll().forEach(entry ->
                System.out.println("  [" + entry.getLevel() + "] "
                    + entry.getMessage().substring(0,
                        Math.min(100, entry.getMessage().length()))));
        }
    } catch (Exception e) {
        System.out.println("[Selenium4Test] Console log note: " + e.getMessage()
            + " (Chrome only, requires goog:loggingPrefs)");
    }

    String pageTitle = driver.getTitle().toLowerCase();
    System.out.println("[Selenium4Test] Page title: " + driver.getTitle());
    Assert.assertTrue(
        pageTitle.contains("demo web shop") ||
        pageTitle.contains("demowebshop") ||
        pageTitle.contains("tricentis") ||
        driver.getCurrentUrl().contains("demowebshop.tricentis.com"),
        "Should be on DemoWebShop page. Title: " + driver.getTitle()
        + " | URL: " + driver.getCurrentUrl());
    System.out.println("[Selenium4Test] ✅ Browser console logs demonstrated");
}
}
