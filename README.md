# Selenium Automation Framework

> End-to-end test automation for a real-world e-commerce platform — built to solve production-level challenges in multi-browser testing, dynamic content handling, and robust synchronization.

![Java](https://img.shields.io/badge/Java-11-orange?style=flat-square&logo=java)
![Selenium](https://img.shields.io/badge/Selenium-4.18.1-brightgreen?style=flat-square&logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.9.0-red?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-3.9-blue?style=flat-square&logo=apachemaven)
![Tests](https://img.shields.io/badge/Tests-19%20Passed-success?style=flat-square)
![CI](https://github.com/SahilSR81/Selenium-Java-Automation-Framework/actions/workflows/tests.yml/badge.svg)

---

## What Problem Does This Solve?

Manual testing of e-commerce flows is slow, error-prone, and impossible to scale across browsers. This framework automates the complete user journey — from registration to checkout — and handles real-world challenges like:

- **Bot detection** on automated browsers (Chrome stealth options applied)
- **AJAX-heavy checkout flows** where DOM loads asynchronously
- **Flaky tests** caused by network delays (auto-retry mechanism)
- **Cross-browser inconsistencies** across Chrome, Firefox, and Edge
- **Test data management** without hardcoding credentials

---

## Impact

- Automated 19+ test cases covering critical e-commerce workflows  
- Reduced manual testing effort by ~40% through automation  
- Improved test reliability using retry logic and smart waits  
- Enabled parallel execution reducing execution time by ~50%  

---

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 11 | Core language |
| Selenium WebDriver | 4.18.1 | Browser automation |
| TestNG | 7.9.0 | Test orchestration & parallel execution |
| Maven | 3.9 | Build & dependency management |
| ExtentReports | 5.1.1 | Rich HTML test reports with screenshots |
| Apache POI | 5.2.3 | Excel-based data-driven testing |
| OpenCSV | 5.8 | CSV-based data-driven testing |
| WebDriverManager | 5.7.0 | Automatic browser driver management |

---

## Key Features

- **Multi-browser support** — Chrome, Firefox, Edge via WebDriverManager
- **Parallel execution** — Thread-safe WebDriver with TestNG parallel classes
- **Data-driven testing** — Excel (Apache POI) and CSV (OpenCSV) DataProviders
- **Smart wait strategies** — Implicit, Explicit, and Fluent waits for synchronization
- **Selenium 4 features** — Relative locators, new tab/window handling, console log capture
- **Auto-retry mechanism** — Flaky test recovery (max 2 retries)
- **Screenshot on failure** — Auto-captured and embedded in ExtentReports
- **Externalized config** — Environment settings in `config.properties`, no hardcoded values
- **Full e-commerce flow** — Registration → Login → Add to Cart → Checkout → Logout

---

## Test Coverage

| Test Class | Test Methods | What It Covers |
|------------|-------------|----------------|
| `SignUpTest` | 1 | User registration with form validation |
| `LoginTest` | 5 + 3 CSV rows | Valid/invalid login, hover, double-click, Excel & CSV data-driven |
| `CheckoutTest` | 1 | Complete cart → checkout → order confirmation |
| `WaitDemoTest` | 3 | Implicit, explicit, and fluent wait strategies |
| `Selenium4Test` | 4 | Relative locators, new tab/window, browser console logs |
| `LocatorDemoTest` | 1 | Absolute vs relative XPath comparison |
| **Total** | **19 tests** | **All PASS** |

---

## Project Structure

```
Selenium_Automation_Framework/
├── pom.xml
├── README.md
├── SAF_Project_Report.pdf
├── src/
│   ├── main/java/com/saf/
│   │   ├── base/BaseTest.java          # WebDriver init, lifecycle management
│   │   ├── listeners/TestListener.java  # ExtentReports + screenshot on failure
│   │   └── utils/
│   │       ├── ConfigReader.java        # Singleton config management
│   │       ├── WaitUtil.java            # Implicit, explicit, fluent waits
│   │       ├── ScreenshotUtil.java      # Failure screenshot capture
│   │       ├── ExcelUtil.java           # Apache POI Excel handler
│   │       └── RetryAnalyzer.java       # Auto-retry for flaky tests
│   └── test/
│       ├── java/com/saf/tests/
│       │   ├── SignUpTest.java
│       │   ├── LoginTest.java
│       │   ├── CheckoutTest.java
│       │   ├── WaitDemoTest.java
│       │   ├── Selenium4Test.java
│       │   └── LocatorDemoTest.java
│       └── resources/
│           ├── config.properties        # Base URL, browser, credentials
│           ├── testng.xml               # Suite config with parallel execution
│           ├── testdata.xlsx            # Excel test data
│           ├── testdata.csv             # CSV test data
│           └── drivers/msedgedriver.exe # Local Edge driver
├── reports/ExtentReport.html           # Generated after test run
└── test-output/
    ├── screenshots/                     # Captured on failure
    └── index.html                       # TestNG native report
```

---

## How to Run

### Prerequisites

- Java 11 JDK — set `JAVA_HOME` in environment variables
- Maven — added to system `PATH`

### Run Commands

```bash
# Full test suite (Chrome by default)
mvn clean test

# Specific browser
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge

# Single test class
mvn clean test -Dtest=LoginTest
mvn clean test -Dtest=SignUpTest
mvn clean test -Dtest=CheckoutTest

# By group
mvn clean test -Dgroups=smoke
mvn clean test -Dgroups=datadriven
```

---

## View Reports

After running tests:

```bash
# Extent Report (rich HTML with screenshots)
open reports/ExtentReport.html

# TestNG native report
open test-output/index.html

# Failure screenshots
open test-output/screenshots/
```

---

## Troubleshooting

| Issue | Fix |
|-------|-----|
| `mvn` not recognized | Add Maven `bin` folder to PATH, restart terminal |
| Tests not in Test Explorer (VS Code) | `Ctrl+Shift+P` → Java: Clean Language Server Workspace |
| Red errors after import | Right-click `pom.xml` → Reload Project |
| WebDriver error | WebDriverManager downloads automatically — check internet connection |
| `JAVA_HOME` not set | Set it in System Environment Variables pointing to JDK 11 |
| Edge driver missing | Download from [Microsoft Edge WebDriver](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/) and place in `src/test/resources/drivers/` |

---

## Architecture Highlights

**Thread-safe parallel execution** — `ThreadLocal<WebDriver>` ensures each test thread gets its own browser instance with no interference.

**Layered design** — `BaseTest` handles browser lifecycle, utility classes handle cross-cutting concerns (waits, screenshots, config), and test classes stay focused on business logic only.

**Flexible browser selection** — Priority order: JVM arg (`-Dbrowser`) → TestNG parameter → `config.properties`. No code change needed to switch environments.

**Retry logic** — `RetryAnalyzer` catches transient failures (network delays, timing issues) and retries up to 2 times before marking a test as failed.

---

## Author

**Sahil Singh**
Aspiring SDET | Automation Testing | Selenium | API Testing 
Target application: [Demo Web Shop](https://demowebshop.tricentis.com)
