package com.saf.tests;

import com.saf.base.BaseTest;
import com.saf.utils.ConfigReader;
import com.saf.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

/*
 * LocatorDemoTest — Demonstrates absolute vs relative XPath fragility
 * Shows why relative locators are preferred over absolute paths
 */
public class LocatorDemoTest extends BaseTest {

    @Test(groups = {"regression"}, priority = 9)
    public void testAbsoluteVsRelativeXPath() {
        driver.get(ConfigReader.get("BASE_URL"));
        WaitUtil wait = new WaitUtil(driver);

        // absolute xpath — starts from root, traces every parent position
        String absoluteXPath = "/html/body/div[1]/div[1]/div[1]/a/img";
        System.out.println("[LocatorDemoTest] trying absolute xpath: " + absoluteXPath);

        WebElement logoByAbsolute = null;
        try {
            logoByAbsolute = wait.waitForVisible(By.xpath(absoluteXPath), 10);
            System.out.println("[LocatorDemoTest] absolute xpath worked — tag: " + logoByAbsolute.getTagName());
        } catch (Exception e) {
            // this is expected to fail sometimes — that's the whole point of this demo
            System.out.println("[LocatorDemoTest] absolute xpath failed (" + e.getClass().getSimpleName() + "), which proves it's fragile");
        }

        // relative xpath is safer — uses class name instead of exact position
        String relativeXPath = "//div[contains(@class,'header-logo')]//img";
        WebElement logoByRelative = wait.waitForVisible(By.xpath(relativeXPath), 10);
        System.out.println("[LocatorDemoTest] relative xpath found: " + logoByRelative.getTagName() + " | alt: " + logoByRelative.getAttribute("alt"));

        // absolute = breaks if any parent div shifts position
        // relative = still works even if page structure changes slightly
        Assert.assertTrue(logoByRelative.isDisplayed(), "logo should be found via relative xpath");
        System.out.println("[LocatorDemoTest] absolute vs relative xpath demo done");
    }
}
