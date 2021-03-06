package com.example.superduperdrive;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SuperDuperDriveApplicationTests {

    private static final String DEMO_USER_USERNAME = "admin";
    private static final String DEMO_USER_PASSWORD = "123456";
    private static final String DEMO_USER_FIRST_NAME = "Peter";
    private static final String DEMO_USER_LAST_NAME = "Parker";

    private static final String DEMO_NOTE_TITLE = "This is the title";
    private static final String DEMO_NOTE_DESCRIPTION = "This is the description";

    private static final String DEMO_NOTE_ALT_TITLE = "This is the alternate title";
    private static final String DEMO_NOTE_ALT_DESCRIPTION = "This is the alternate description";

    private static final String DEMO_CREDENTIAL_URL = "http://www.gmail.com";
    private static final String DEMO_CREDENTIAL_USERNAME = "dummy@gmail.com";
    private static final String DEMO_CREDENTIAL_PASSWORD = "123456";

    private static final String DEMO_CREDENTIAL_ALT_URL = "http://www.gmail.com";
    private static final String DEMO_CREDENTIAL_ALT_USERNAME = "changed@gmail.com";
    private static final String DEMO_CREDENTIAL_ALT_PASSWORD = "123456789";

    @LocalServerPort
    private int port;

    public static WebDriver driver;
    public String baseUrl;

    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver();
    }

    @AfterAll
    public static void afterAll() {
        driver.quit();
        driver = null;
    }

    @BeforeEach
    public void beforeEach() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @Order(1)
    public void getDashboardWithoutLogin() {
        driver.get(baseUrl);

        assertThat(driver.getTitle()).isEqualTo("Login");
    }

    @Test
    @Order(2)
    public void signupUserLoginAndGoToDashboard() {
        // Sign up the user
        driver.get(baseUrl + "/signup");

        SignupPage signupPage = new SignupPage(driver);

        assertThat(driver.getTitle()).isEqualTo("Sign Up");

        signupPage.signup(DEMO_USER_FIRST_NAME, DEMO_USER_LAST_NAME, DEMO_USER_USERNAME,
                DEMO_USER_PASSWORD);

        // Login the user
        driver.get(baseUrl + "/login");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(DEMO_USER_USERNAME, DEMO_USER_PASSWORD);

        // Verify that the user can access the home page
        driver.get(baseUrl);

        HomePage homePage = new HomePage(driver);

        assertThat(driver.getTitle()).isEqualTo("Home");

        // Log out the user
        homePage.logout();

        // Verify that the home page is no longer accessible
        driver.get(baseUrl);

        assertThat(driver.getTitle()).isEqualTo("Login");
    }

    @Test
    @Order(3)
    public void logsUserCreateNoteAndVerifyItExists() {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        // Step 1: login the user
        driver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(driver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = driver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(driver.getTitle()).isEqualTo("Home");

        // Step 2: Create a note
        JavascriptExecutor executor = (JavascriptExecutor) driver;

        WebElement navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        WebElement addNoteButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonAddNewNote")));
        addNoteButton.click();

        WebElement noteTitleInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
        noteTitleInputField.clear();
        noteTitleInputField.sendKeys(DEMO_NOTE_TITLE);

        WebElement noteDescriptionInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
        noteDescriptionInputField.clear();
        noteDescriptionInputField.sendKeys(DEMO_NOTE_DESCRIPTION);

        WebElement saveNoteButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveNoteButton")));

        saveNoteButton.click();

        WebElement successDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("divSuccess")));

        assertThat(driver.getTitle()).isEqualTo("Result");

        // Step 3: Check if the note is listed
        driver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(driver.getTitle()).isEqualTo("Home");

        navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        assertThat(navNotesTab.getText().contains(DEMO_NOTE_TITLE));
        assertThat(navNotesTab.getText().contains(DEMO_NOTE_DESCRIPTION));
    }

    @Test
    @Order(4)
    public void updateExistingNoteAndVerifyChangesAreReflected() {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        // Step 1: login the user
        driver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(driver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = driver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(driver.getTitle()).isEqualTo("Home");

        // Step 2: Edit the note
        JavascriptExecutor executor = (JavascriptExecutor) driver;

        WebElement navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        List<WebElement> editButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("edit-note-button")));

        if (editButtons.size() > 0) {
            editButtons.get(0).click();

            WebElement titleInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
            titleInputField.clear();
            titleInputField.sendKeys(DEMO_NOTE_ALT_TITLE);

            WebElement descriptionInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-description")));
            descriptionInputField.clear();
            descriptionInputField.sendKeys(DEMO_NOTE_ALT_DESCRIPTION);

            WebElement saveNoteButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveNoteButton")));

            saveNoteButton.click();

            WebElement successDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("divSuccess")));

            assertThat(driver.getTitle()).isEqualTo("Result");
        }

        // Step 3: Check that the note changes are reflected.
        driver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(driver.getTitle()).isEqualTo("Home");

        navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        assertThat(navNotesTab.getText().contains(DEMO_NOTE_ALT_TITLE));
        assertThat(navNotesTab.getText().contains(DEMO_NOTE_ALT_DESCRIPTION));
    }

    @Test
    @Order(5)
    public void deleteExistingNoteAndVerifyItIsNoLongerListed() {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        // Step 1: login the user
        driver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(driver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = driver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(driver.getTitle()).isEqualTo("Home");

        // Step 2: Delete the note
        JavascriptExecutor executor = (JavascriptExecutor) driver;

        WebElement navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        List<WebElement> deleteButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("delete-note-button")));

        if (deleteButtons.size() > 0) {
            deleteButtons.get(0).click();

            WebElement successDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("divSuccess")));

            assertThat(driver.getTitle()).isEqualTo("Result");
        }

        // Step 3: Check that the note is no longer listed.
        driver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(driver.getTitle()).isEqualTo("Home");

        navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        assertThat(navNotesTab.getText().contains(DEMO_NOTE_ALT_TITLE)).isFalse();
        assertThat(navNotesTab.getText().contains(DEMO_NOTE_ALT_DESCRIPTION)).isFalse();
    }

    @Test
    @Order(6)
    public void logsUserCreateCredentialAndVerifyItExists() {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        // Step 1: login the user
        driver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(driver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = driver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(driver.getTitle()).isEqualTo("Home");

        // Step 2: Create the credential
        JavascriptExecutor executor = (JavascriptExecutor) driver;

        WebElement navCredentialsTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));

        executor.executeScript("arguments[0].click()", navCredentialsTab);

        WebElement addCredentialButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonAddNewCredential")));
        addCredentialButton.click();

        WebElement credentialUrlInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
        credentialUrlInputField.clear();
        credentialUrlInputField.sendKeys(DEMO_CREDENTIAL_URL);

        WebElement credentialUsernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-username")));
        credentialUsernameInputField.clear();
        credentialUsernameInputField.sendKeys(DEMO_CREDENTIAL_USERNAME);

        WebElement credentialPasswordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
        credentialPasswordInputField.clear();
        credentialPasswordInputField.sendKeys(DEMO_CREDENTIAL_PASSWORD);

        WebElement saveCredentialButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveCredentialButton")));

        saveCredentialButton.click();

        WebElement successDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("divSuccess")));

        assertThat(driver.getTitle()).isEqualTo("Result");

        // Step 3: Check if the credential is listed
        driver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(driver.getTitle()).isEqualTo("Home");

        navCredentialsTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));

        executor.executeScript("arguments[0].click()", navCredentialsTab);

        assertThat(navCredentialsTab.getText().contains(DEMO_CREDENTIAL_URL));
        assertThat(navCredentialsTab.getText().contains(DEMO_CREDENTIAL_USERNAME));
    }

    @Test
    @Order(7)
    public void updateExistingCredentialAndVerifyChangesAreReflected() {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        // Step 1: login the user
        driver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(driver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = driver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(driver.getTitle()).isEqualTo("Home");

        // Step 2: Edit the credential
        JavascriptExecutor executor = (JavascriptExecutor) driver;

        WebElement navCredentialsTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));

        executor.executeScript("arguments[0].click()", navCredentialsTab);

        List<WebElement> editButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("edit-credential-button")));

        if (editButtons.size() > 0) {
            editButtons.get(0).click();

            WebElement credentialUrlInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
            credentialUrlInputField.clear();
            credentialUrlInputField.sendKeys(DEMO_CREDENTIAL_ALT_URL);

            WebElement credentialUsernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-username")));
            credentialUsernameInputField.clear();
            credentialUsernameInputField.sendKeys(DEMO_CREDENTIAL_ALT_USERNAME);

            WebElement credentialPasswordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));
            credentialPasswordInputField.clear();
            credentialPasswordInputField.sendKeys(DEMO_CREDENTIAL_ALT_PASSWORD);

            WebElement saveCredentialButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveCredentialButton")));

            saveCredentialButton.click();

            WebElement successDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("divSuccess")));

            assertThat(driver.getTitle()).isEqualTo("Result");
        }

        // Step 3: Check that credential changes are reflected
        driver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(driver.getTitle()).isEqualTo("Home");

        navCredentialsTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));

        executor.executeScript("arguments[0].click()", navCredentialsTab);

        assertThat(navCredentialsTab.getText().contains(DEMO_CREDENTIAL_URL));
        assertThat(navCredentialsTab.getText().contains(DEMO_CREDENTIAL_USERNAME));
    }

    @Test
    @Order(8)
    public void deleteExistingCredentialAndVerifyItIsNoLongerListed() {
        WebDriverWait wait = new WebDriverWait(driver, 10);

        // Step 1: login the user
        driver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(driver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = driver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(driver.getTitle()).isEqualTo("Home");

        // Step 2: Delete the credential
        JavascriptExecutor executor = (JavascriptExecutor) driver;

        WebElement navCredentialsTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));

        executor.executeScript("arguments[0].click()", navCredentialsTab);

        List<WebElement> deleteButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("delete-credential-button")));

        if (deleteButtons.size() > 0) {
            deleteButtons.get(0).click();

            WebElement successDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("divSuccess")));

            assertThat(driver.getTitle()).isEqualTo("Result");
        }

        // Step 3: Check that the credential is no longer listed.
        driver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(driver.getTitle()).isEqualTo("Home");

        navCredentialsTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));

        executor.executeScript("arguments[0].click()", navCredentialsTab);

        assertThat(navCredentialsTab.getText().contains(DEMO_CREDENTIAL_ALT_URL)).isFalse();
        assertThat(navCredentialsTab.getText().contains(DEMO_CREDENTIAL_ALT_USERNAME)).isFalse();
    }
}
