package com.saf.utils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

// Screenshot utility for capturing screenshots during test execution
public class ScreenshotUtil {

private static final String SCREENSHOT_DIR = "test-output/screenshots/";

// Captures a screenshot and saves it with a unique name based on the test name and timestamp
public static String captureScreenshot(WebDriver driver, String testName) {
    if (driver == null) {
        System.err.println("[ScreenshotUtil] Driver is null — skipping screenshot");
        return "";
    }
    try {
        File dir = new File(SCREENSHOT_DIR);
        if (!dir.exists()) dir.mkdirs();

        // Generate timestamp for unique filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName  = testName + "_" + timestamp + ".png";
        String fullPath  = SCREENSHOT_DIR + fileName;

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(src.toPath(),
            new File(fullPath).toPath(),
            StandardCopyOption.REPLACE_EXISTING);

        System.out.println("[ScreenshotUtil] Saved: " + fullPath);
        return new File(fullPath).getAbsolutePath();

    } catch (IOException e) {
        // Log any exceptions during file operations
        System.err.println("[ScreenshotUtil] Failed: " + e.getMessage());
        return "";
    }
}
}
