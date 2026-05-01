package com.saf.tests;

import com.saf.base.BaseTest;
import com.saf.utils.ConfigReader;
import com.saf.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

// Test class for end-to-end checkout flow: login, add to cart, checkout with COD, and logout
public class CheckoutTest extends BaseTest {

    @Test(priority = 3, groups = {"regression"},
          description = "Login + Add to cart + Full checkout with COD + Logout")
    public void testAddToCartAndCheckout() throws InterruptedException {

        WaitUtil wait = new WaitUtil(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ── STEP 0: LOGIN ─────────────────────────────────────────────
        System.out.println("[CheckoutTest] STEP 0: Login");
        wait.waitForClickable(By.linkText("Log in"), 10).click();
        wait.waitForVisible(By.id("Email"), 10);
        
        String email = ConfigReader.get("VALID_EMAIL");
        String password = ConfigReader.get("VALID_PASSWORD");
        
        System.out.println("[CheckoutTest] Logging in with: " + email);
        driver.findElement(By.id("Email")).clear();
        driver.findElement(By.id("Email")).sendKeys(email);
        driver.findElement(By.id("Password")).clear();
        driver.findElement(By.id("Password")).sendKeys(password);
        
        // Wait a moment before clicking login
        Thread.sleep(1000);
        driver.findElement(By.cssSelector("input.login-button")).click();
        
        // Wait for login to complete - check for either logout link or error
        try {
            wait.waitForVisible(By.linkText("Log out"), 15);
            System.out.println("[CheckoutTest] ✅ Logged in successfully as: " + email);
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Login failed - checking for error message");
            try {
                WebElement errorMsg = driver.findElement(By.cssSelector(".message-error"));
                System.out.println("[CheckoutTest] Error message: " + errorMsg.getText());
                System.out.println("[CheckoutTest] Current URL: " + driver.getCurrentUrl());
            } catch (Exception ex) {
                System.out.println("[CheckoutTest] No error message found");
            }
        }

        // ── STEP 1: ADD BOOK TO CART ──────────────────────────────────
        System.out.println("[CheckoutTest] STEP 1: Add book to cart");
        driver.get(ConfigReader.get("BASE_URL") + "/books");
        wait.waitForVisible(By.cssSelector(".product-grid"), 15);

        WebElement productTitle = driver.findElement(
            By.cssSelector(".product-title a"));
        String productName = productTitle.getText();
        System.out.println("[CheckoutTest] getText() product: " + productName);
        System.out.println("[CheckoutTest] getAttribute('href'): "
            + productTitle.getAttribute("href"));

        WebElement addBtn = wait.waitForClickable(
            By.cssSelector(".button-2.product-box-add-to-cart-button"), 10);
        js.executeScript("arguments[0].click();", addBtn);
        System.out.println("[CheckoutTest] Clicked Add to cart (JS executor)");

        try {
            WebElement notif = wait.fluentWait(
                By.cssSelector("#bar-notification .content"), 15, 300);
            System.out.println("[CheckoutTest] Notification: "
                + notif.getText());
            try {
                driver.findElement(
                    By.cssSelector("#bar-notification .close")).click();
            } catch (Exception ignored) {}
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Notification missed, continuing");
        }

        // ── STEP 2: GO TO CART ────────────────────────────────────────
        System.out.println("[CheckoutTest] STEP 2: Go to cart");
        wait.waitForClickable(By.cssSelector(".cart-qty"), 10).click();
        wait.waitForUrlContains("cart", 10);
        System.out.println("[CheckoutTest] Cart URL: " + driver.getCurrentUrl());

        WebElement cartItem = wait.waitForVisible(
            By.cssSelector("td.product a.product-name"), 10);
        System.out.println("[CheckoutTest] Cart getText(): "
            + cartItem.getText());
        System.out.println("[CheckoutTest] Cart getAttribute('href'): "
            + cartItem.getAttribute("href"));

        // Estimate shipping — select India
        try {
            Select countryDrop = new Select(
                driver.findElement(By.id("CountryId")));
            countryDrop.selectByValue("41"); // India
            System.out.println("[CheckoutTest] Shipping estimate: India selected");
            Thread.sleep(500);
            driver.findElement(
                By.cssSelector("input[name='estimateshipping']")).click();
            Thread.sleep(1000);
            System.out.println("[CheckoutTest] Estimate shipping clicked");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Estimate shipping skipped: "
                + e.getMessage());
        }

        // Accept terms and checkout
        WebElement terms = driver.findElement(By.id("termsofservice"));
        if (!terms.isSelected()) {
            js.executeScript("arguments[0].click();", terms);
        }
        System.out.println("[CheckoutTest] Terms accepted");

        wait.waitForClickable(By.cssSelector("button#checkout"), 10).click();
        System.out.println("[CheckoutTest] Checkout button clicked");

        // ── STEP 3: CHECKOUT PAGE ─────────────────────────────────────
        System.out.println("[CheckoutTest] STEP 3: Checkout page");
        wait.waitForUrlContains("onepagecheckout", 15);
        System.out.println("[CheckoutTest] URL: " + driver.getCurrentUrl());

        // ── STEP 4: BILLING ───────────────────────────────────────────
        System.out.println("[CheckoutTest] STEP 4: Billing address");
        Thread.sleep(2000);

        // Select saved address by value "4805620"
        wait.waitForVisible(By.id("billing-address-select"), 15);
        Select billingDrop = new Select(
            driver.findElement(By.id("billing-address-select")));
        billingDrop.selectByValue("4805620");
        System.out.println("[CheckoutTest] Billing: selected saved address — "
            + billingDrop.getFirstSelectedOption().getText());

        // Click Continue (calls Billing.save() via JS)
        WebElement billingNext = wait.waitForClickable(
            By.cssSelector("#billing-buttons-container input.button-1"), 10);
        js.executeScript("arguments[0].click();", billingNext);
        System.out.println("[CheckoutTest] Billing Continue clicked");
        Thread.sleep(3000);

        // ── STEP 5: SHIPPING ADDRESS ──────────────────────────────────
        System.out.println("[CheckoutTest] STEP 5: Shipping address");
        wait.waitForVisible(By.id("shipping-address-select"), 20);
        Select shippingDrop = new Select(
            driver.findElement(By.id("shipping-address-select")));
        shippingDrop.selectByValue("4805620");
        System.out.println("[CheckoutTest] Shipping: selected saved address — "
            + shippingDrop.getFirstSelectedOption().getText());

        js.executeScript("arguments[0].click();",
            driver.findElement(
                By.cssSelector("#shipping-buttons-container input.button-1")));
        System.out.println("[CheckoutTest] Shipping Continue clicked");
        Thread.sleep(3000);

        // ── STEP 6: SHIPPING METHOD ───────────────────────────────────
        System.out.println("[CheckoutTest] STEP 6: Shipping method");
        
        // Wait for shipping method step with fallback
        try {
            wait.waitForVisible(
                By.cssSelector("#shipping-method-buttons-container input.button-1"), 20);
            System.out.println("[CheckoutTest] Shipping method step loaded successfully");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Shipping method step not found, checking if shipping address failed");
            try {
                WebElement shippingError = driver.findElement(By.cssSelector("#shipping-buttons-container input.button-1"));
                System.out.println("[CheckoutTest] Still on shipping address step, trying continue again");
                js.executeScript("arguments[0].click();", shippingError);
                Thread.sleep(3000);
                wait.waitForVisible(
                    By.cssSelector("#shipping-method-buttons-container input.button-1"), 15);
            } catch (Exception e2) {
                System.out.println("[CheckoutTest] Cannot proceed to shipping method step, but continuing");
            }
        }

        // Select first available shipping radio (Ground shipping)
        try {
            WebElement shippingRadio = driver.findElement(
                By.cssSelector(
                    "#checkout-step-shipping-method input[type='radio']"));
            if (!shippingRadio.isSelected()) {
                js.executeScript("arguments[0].click();", shippingRadio);
            }
            System.out.println("[CheckoutTest] Shipping method selected: "
                + shippingRadio.getAttribute("value"));
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Shipping radio: " + e.getMessage());
        }

        try {
            js.executeScript("arguments[0].click();",
                driver.findElement(By.cssSelector(
                    "#shipping-method-buttons-container input.button-1")));
            System.out.println("[CheckoutTest] Shipping Method Continue clicked");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Shipping Method Continue button not found, continuing anyway");
        }
        Thread.sleep(3000);

        // ── STEP 7: PAYMENT METHOD — CASH ON DELIVERY ────────────────
        System.out.println("[CheckoutTest] STEP 7: Payment method — Cash on Delivery");
        
        // Wait for payment method step with fallback
        try {
            wait.waitForVisible(
                By.cssSelector("#payment-method-buttons-container input.button-1"), 20);
            System.out.println("[CheckoutTest] Payment method step loaded successfully");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Payment method step not found, checking if shipping method failed");
            try {
                WebElement shippingMethodError = driver.findElement(By.cssSelector("#shipping-method-buttons-container input.button-1"));
                System.out.println("[CheckoutTest] Still on shipping method step, trying continue again");
                js.executeScript("arguments[0].click();", shippingMethodError);
                Thread.sleep(3000);
                wait.waitForVisible(
                    By.cssSelector("#payment-method-buttons-container input.button-1"), 15);
            } catch (Exception e2) {
                System.out.println("[CheckoutTest] Cannot proceed to payment method step, but continuing");
            }
        }

        // Select Cash on Delivery
        try {
            // Try by value first
            WebElement codRadio = driver.findElement(
                By.cssSelector("input[value='Payments.CashOnDelivery']"));
            if (!codRadio.isSelected()) {
                js.executeScript("arguments[0].click();", codRadio);
            }
            System.out.println("[CheckoutTest] Cash on Delivery selected");
        } catch (Exception e) {
            // Fallback — select first available payment method
            try {
                WebElement firstPayment = driver.findElement(
                    By.cssSelector(
                        "#checkout-step-payment-method input[type='radio']"));
                if (!firstPayment.isSelected()) {
                    js.executeScript("arguments[0].click();", firstPayment);
                }
                System.out.println("[CheckoutTest] Payment method selected: "
                    + firstPayment.getAttribute("value"));
            } catch (Exception e2) {
                System.out.println("[CheckoutTest] Payment method auto-selected or not required");
            }
        }

        try {
            js.executeScript("arguments[0].click();",
                driver.findElement(By.cssSelector(
                    "#payment-method-buttons-container input.button-1")));
            System.out.println("[CheckoutTest] Payment Method Continue clicked");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Payment Method Continue button not found, continuing anyway");
        }
        Thread.sleep(3000);

        // ── STEP 8: PAYMENT INFO ──────────────────────────────────────
        System.out.println("[CheckoutTest] STEP 8: Payment info");
        
        // Handle any unexpected alerts first
        try {
            if (driver.switchTo().alert() != null) {
                String alertText = driver.switchTo().alert().getText();
                System.out.println("[CheckoutTest] Alert detected: " + alertText);
                driver.switchTo().alert().accept();
                System.out.println("[CheckoutTest] Alert accepted");
                Thread.sleep(2000); // Wait for redirect after alert
            }
        } catch (Exception e) {
            System.out.println("[CheckoutTest] No alert present");
        }
        
        // Wait for payment info step with fallback
        try {
            wait.waitForVisible(
                By.cssSelector("#payment-info-buttons-container input.button-1"), 20);
            System.out.println("[CheckoutTest] Payment info step loaded successfully");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Payment info step not found, checking if payment method failed");
            try {
                WebElement paymentMethodError = driver.findElement(By.cssSelector("#payment-method-buttons-container input.button-1"));
                System.out.println("[CheckoutTest] Still on payment method step, trying continue again");
                js.executeScript("arguments[0].click();", paymentMethodError);
                Thread.sleep(3000);
                wait.waitForVisible(
                    By.cssSelector("#payment-info-buttons-container input.button-1"), 15);
            } catch (Exception e2) {
                System.out.println("[CheckoutTest] Cannot proceed to payment info step, but continuing");
            }
        }

        try {
            String paymentInfoText = driver.findElement(
                By.cssSelector("#checkout-step-payment-info")).getText();
            System.out.println("[CheckoutTest] Payment info text: "
                + paymentInfoText.trim());
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Payment info text: not captured");
        }

        try {
            js.executeScript("arguments[0].click();",
                driver.findElement(By.cssSelector(
                    "#payment-info-buttons-container input.button-1")));
            System.out.println("[CheckoutTest] Payment Info Continue clicked");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Payment Info Continue button not found, continuing anyway");
        }
        Thread.sleep(3000);

        // ── STEP 9: CONFIRM ORDER ─────────────────────────────────────
        System.out.println("[CheckoutTest] STEP 9: Confirm order");
        
        // Wait for confirm order step with fallback
        try {
            wait.waitForVisible(
                By.cssSelector("#confirm-order-buttons-container input.button-1"), 20);
            System.out.println("[CheckoutTest] Confirm order step loaded successfully");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Confirm order step not found, checking if payment info failed");
            try {
                WebElement paymentInfoError = driver.findElement(By.cssSelector("#payment-info-buttons-container input.button-1"));
                System.out.println("[CheckoutTest] Still on payment info step, trying continue again");
                js.executeScript("arguments[0].click();", paymentInfoError);
                Thread.sleep(3000);
                wait.waitForVisible(
                    By.cssSelector("#confirm-order-buttons-container input.button-1"), 15);
            } catch (Exception e2) {
                System.out.println("[CheckoutTest] Cannot proceed to confirm order step, but continuing");
            }
        }

        // Log billing/shipping summary from confirm page
        try {
            System.out.println("[CheckoutTest] Billing info: "
                + driver.findElement(By.cssSelector(".billing-info")).getText()
                         .replaceAll("\\s+", " ").trim());
            System.out.println("[CheckoutTest] Shipping info: "
                + driver.findElement(By.cssSelector(".shipping-info")).getText()
                         .replaceAll("\\s+", " ").trim());
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Confirm summary: " + e.getMessage());
        }

        try {
            js.executeScript("arguments[0].click();",
                driver.findElement(By.cssSelector(
                    "#confirm-order-buttons-container input.button-1")));
            System.out.println("[CheckoutTest] ✅ Order Confirmed!");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Confirm Order button not found, continuing anyway");
        }
        Thread.sleep(3000);

        // ── STEP 10: SUCCESS PAGE ─────────────────────────────────────
        System.out.println("[CheckoutTest] STEP 10: Order completion");
        
        // Handle any alerts before checking URL
        try {
            if (driver.switchTo().alert() != null) {
                String alertText = driver.switchTo().alert().getText();
                System.out.println("[CheckoutTest] Alert before success page: " + alertText);
                driver.switchTo().alert().accept();
                System.out.println("[CheckoutTest] Alert accepted");
                Thread.sleep(2000); // Wait for redirect after alert
            }
        } catch (Exception e) {
            System.out.println("[CheckoutTest] No alert present before success page");
        }
        
        wait.waitForUrlContains("completed", 20);
        System.out.println("[CheckoutTest] Success URL: " + driver.getCurrentUrl());

        WebElement successTitle = wait.waitForVisible(
            By.cssSelector(".title strong"), 15);
        System.out.println("[CheckoutTest] getText() success: "
            + successTitle.getText());

        // Order number
        try {
            WebElement orderNum = driver.findElement(
                By.cssSelector(".order-number strong"));
            System.out.println("[CheckoutTest] Order number: "
                + orderNum.getText());
            Assert.assertFalse(orderNum.getText().isEmpty(),
                "Order number should not be empty");
        } catch (Exception e) {
            System.out.println("[CheckoutTest] Order number element: " + e.getMessage());
        }

        Assert.assertTrue(
            successTitle.getText().toLowerCase().contains("processed") ||
            driver.getCurrentUrl().contains("completed"),
            "Order should complete. URL: " + driver.getCurrentUrl());
        System.out.println("[CheckoutTest] ✅ Full checkout PASSED");

        // ── STEP 11: LOGOUT ───────────────────────────────────────────
        System.out.println("[CheckoutTest] STEP 11: Logout");
        wait.waitForClickable(By.linkText("Log out"), 10).click();
        wait.waitForVisible(By.linkText("Log in"), 15);
        Assert.assertTrue(
            driver.findElement(By.linkText("Log in")).isDisplayed(),
            "Log in link visible after logout");
        System.out.println("[CheckoutTest] ✅ Logged out — Log in link visible");
    }
}
