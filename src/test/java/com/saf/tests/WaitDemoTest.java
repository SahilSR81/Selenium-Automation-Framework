package com.saf.tests;
import com.saf.base.BaseTest;
import com.saf.utils.ConfigReader;
import com.saf.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

// Test class demonstrating different wait strategies: implicit, explicit, and fluent waits
public class WaitDemoTest extends BaseTest {

// -------------------------------------------------------
// TEST 1: Implicit Wait — demonstration of limitation
// -------------------------------------------------------
@Test(groups = {"waits"}, priority = 9)
public void testImplicitWait() {
    driver.get(ConfigReader.get("BASE_URL"));
    WaitUtil waitUtil = new WaitUtil(driver);

    // Set implicit wait to 2 seconds (very short intentionally)
    waitUtil.setImplicitWait(2);
    System.out.println("[WaitDemoTest] IMPLICIT WAIT set to 2 seconds");
    System.out.println("[WaitDemoTest] Problem: implicit wait cannot wait "
        + "for a specific condition — just polls DOM blindly");

    try {
        // This element is always present on homepage — should succeed
        // HOME_PRODUCT_TITLE -> cssSelector=".product-title a"
        WebElement product = driver.findElement(
            By.cssSelector(".product-title a"));
        System.out.println("[WaitDemoTest] Implicit wait found: "
            + product.getText());
        Assert.assertTrue(product.isDisplayed());
    } catch (Exception e) {
        System.out.println("[WaitDemoTest] Implicit wait FAILED (expected in demo): "
            + e.getClass().getSimpleName());
        System.out.println("[WaitDemoTest] This shows why explicit wait is better");
    } finally {
        // Always reset implicit wait so other tests are not affected
        waitUtil.setImplicitWait(10);
        System.out.println("[WaitDemoTest] Implicit wait reset to 10s");
    }
    System.out.println("[WaitDemoTest] ✅ Implicit wait demo complete");
}

// -------------------------------------------------------
// TEST 2: Explicit Wait — proper solution
// -------------------------------------------------------
@Test(groups = {"waits"}, priority = 10)
public void testExplicitWait() {
    // Navigate and wait for page load
    driver.get(ConfigReader.get("BASE_URL") + "/search");
    WaitUtil waitUtil = new WaitUtil(driver);
    
    // Wait for URL to stabilize
    waitUtil.waitForUrlContains("search", 10);
    System.out.println("[WaitDemoTest] EXPLICIT WAIT: waiting for search field");
    System.out.println("[WaitDemoTest] Advantage: waits for SPECIFIC condition, "
        + "not just DOM presence");

    // The search input on /search page has id="q"
    // But try cssSelector as backup if id fails
    WebElement searchInput;
    try {
        searchInput = waitUtil.waitForVisible(By.id("q"), 10);
        System.out.println("[WaitDemoTest] Found search by id='q'");
    } catch (Exception e) {
        System.out.println("[WaitDemoTest] id='q' not found, trying name='q'");
        searchInput = waitUtil.waitForVisible(By.name("q"), 10);
    }
    Assert.assertTrue(searchInput.isDisplayed(),
        "Search input must be visible");
    System.out.println("[WaitDemoTest] Explicit wait resolved: search visible");

    // Type and search
    searchInput.sendKeys("notebook");
    System.out.println("[WaitDemoTest] sendKeys: typed 'notebook'");

    // Try cssSelector first, fallback to xpath for submit button
    try {
        waitUtil.waitForClickable(
            By.cssSelector(".search-button"), 5).click();
        System.out.println("[WaitDemoTest] Clicked search button (cssSelector)");
    } catch (Exception e) {
        // Fallback for /search page submit button
        driver.findElement(
            By.xpath("//input[@value='Search']")).click();
        System.out.println("[WaitDemoTest] Used fallback search submit (xpath)");
    }

        // Wait for URL to contain 'search' — explicit wait on URL condition
    waitUtil.waitForUrlContains("search", 10);
    System.out.println("[WaitDemoTest] URL updated: " + driver.getCurrentUrl());

    // SEARCH_RESULTS -> cssSelector=".search-results"
    WebElement results = waitUtil.waitForVisible(
        By.cssSelector(".search-results"), 20);
    Assert.assertTrue(results.isDisplayed(),
        "Search results must appear");
    System.out.println("[WaitDemoTest] ✅ Explicit wait resolved search results");
    System.out.println("[WaitDemoTest] Explicit wait SOLVED timing issue "
        + "that implicit wait would miss for JS-rendered results");
}

// -------------------------------------------------------
// TEST 3: Fluent Wait — intermittent elements
// -------------------------------------------------------
@Test(groups = {"waits"}, priority = 11)
public void testFluentWait() {
    driver.get(ConfigReader.get("BASE_URL"));
    WaitUtil waitUtil = new WaitUtil(driver);

    System.out.println("[WaitDemoTest] FLUENT WAIT: polls every 500ms for 10s");
    System.out.println("[WaitDemoTest] Best for: AJAX elements, lazy loading, "
        + "elements that appear-disappear-reappear");

    // Fluent wait for logo element (always present, good for demonstration)
    // NAV_LOGO -> cssSelector=".header-logo a"
    WebElement logo = waitUtil.fluentWait(
        By.cssSelector(".header-logo a"), 10, 500);

    Assert.assertTrue(logo.isDisplayed(), "Logo must be visible");
    System.out.println("[WaitDemoTest] Fluent wait found: "
        + logo.getAttribute("href"));
    System.out.println("[WaitDemoTest] ✅ Fluent wait demo complete");
    System.out.println("[WaitDemoTest] Fluent wait advantage: configurable poll "
        + "interval catches elements that appear momentarily");
}
}
