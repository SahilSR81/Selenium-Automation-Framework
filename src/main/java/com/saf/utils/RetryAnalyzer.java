package com.saf.utils;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
/**
RetryAnalyzer: Automatically retries a failed test up to MAX_RETRY times.
Attach to any @Test via: @Test(retryAnalyzer = RetryAnalyzer.class)
How it works: retry() is called by TestNG when a test fails.

If retryCount < MAX_RETRY: increments counter, returns true (retry)
If retryCount >= MAX_RETRY: returns false (stop retrying, mark FAIL)
*/
public class RetryAnalyzer implements IRetryAnalyzer {
private int retryCount = 0;
private static final int MAX_RETRY = 2;

@Override
public boolean retry(ITestResult result) {
    if (retryCount < MAX_RETRY) {
        retryCount++;
        System.out.println("[RetryAnalyzer] Retrying: "
            + result.getName() + " | Attempt: " + retryCount
            + "/" + MAX_RETRY);
        return true;
    }
    System.out.println("[RetryAnalyzer] Max retries reached for: "
        + result.getName());
    return false;
}
}
