package com.saf.utils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

// Wait utility class for explicit and fluent waits
public class WaitUtil {
private final WebDriver driver;

public WaitUtil(WebDriver driver) {
    this.driver = driver;
}

/* Explicit wait — waits until element is visible */
public WebElement waitForVisible(By locator, int timeoutSec) {
    System.out.println("[WaitUtil] Explicit wait: visible -> " + locator);
    return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
        .until(ExpectedConditions.visibilityOfElementLocated(locator));
}

/* Explicit wait — waits until element is clickable */
public WebElement waitForClickable(By locator, int timeoutSec) {
    System.out.println("[WaitUtil] Explicit wait: clickable -> " + locator);
    return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
        .until(ExpectedConditions.elementToBeClickable(locator));
}

/* Explicit wait — waits until URL contains given text */
public boolean waitForUrlContains(String urlFragment, int timeoutSec) {
    System.out.println("[WaitUtil] Explicit wait: URL contains -> " + urlFragment);
    return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
        .until(ExpectedConditions.urlContains(urlFragment));
}

/*
 * Fluent wait — polls every pollMs milliseconds for up to timeoutSec.
 * Ignores NoSuchElementException during polling.
 */
public WebElement fluentWait(By locator, int timeoutSec, int pollMs) {
    System.out.println("[WaitUtil] Fluent wait: " + locator
        + " | timeout=" + timeoutSec + "s | poll=" + pollMs + "ms");
    return new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(timeoutSec))
        .pollingEvery(Duration.ofMillis(pollMs))
        .ignoring(NoSuchElementException.class)
        .until(d -> d.findElement(locator));
}

/* Set implicit wait on the driver */
public void setImplicitWait(int seconds) {
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
    System.out.println("[WaitUtil] Implicit wait set: " + seconds + "s");
}
}
