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

    @LocalServerPort
    private int port;

    private WebDriver webDriver;

    @BeforeAll
    private static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.webDriver = new ChromeDriver();
    }

    @AfterEach
    public void afterEach() {
        if (this.webDriver != null) {
            webDriver.quit();
        }
    }

    @Test
    @Order(1)
    public void getDashboardWithoutLogin() {
        webDriver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Login");
    }

    @Test
    @Order(2)
    public void signUpUserLoginAndGoToDashboard() {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);

        // Step 1: Sign up the user
        webDriver.get(String.format("http://localhost:%s/signup", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Sign Up");

        WebElement firstNameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
        firstNameInputField.sendKeys(DEMO_USER_FIRST_NAME);

        WebElement lastNameNameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
        lastNameNameInputField.sendKeys(DEMO_USER_LAST_NAME);

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement signUpButton = webDriver.findElement(By.id("buttonSignUp"));
        wait.until(ExpectedConditions.elementToBeClickable(signUpButton)).submit();

        WebElement divSuccess = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("divSuccess")));

        assertThat(divSuccess.getText().contains("You successfully signed up!"));

        // Step 2: Login the user and check if it redirects to the home page
        webDriver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Login");

        usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = webDriver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(webDriver.getTitle()).isEqualTo("Home");

        // Step 3: Logout the user and check it redirects to the login page
        logoutButton.click();

        usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));

        assertThat(webDriver.getTitle()).isEqualTo("Login");

        // Step 4: Check if the
        webDriver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Login");
    }

    @Test
    @Order(3)
    public void logsUserCreateNoteAndVerifyItExists() {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);

        // Step 1: login the user
        webDriver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = webDriver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(webDriver.getTitle()).isEqualTo("Home");

        // Step 2: Create a note
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;

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

        assertThat(webDriver.getTitle()).isEqualTo("Result");

        // Step 3: Check if the note is listed
        webDriver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Home");

        navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        assertThat(navNotesTab.getText().contains(DEMO_NOTE_TITLE));
        assertThat(navNotesTab.getText().contains(DEMO_NOTE_DESCRIPTION));
    }

    @Test
    @Order(4)
    public void updateExistingNoteAndVerifyChangesAreReflected() {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);

        // Step 1: login the user
        webDriver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = webDriver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(webDriver.getTitle()).isEqualTo("Home");

        // Step 2: Edit the note

        JavascriptExecutor executor = (JavascriptExecutor) webDriver;

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

            assertThat(webDriver.getTitle()).isEqualTo("Result");
        }

        // Step 3: Check that the note changes are reflected.
        webDriver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Home");

        navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        assertThat(navNotesTab.getText().contains(DEMO_NOTE_ALT_TITLE));
        assertThat(navNotesTab.getText().contains(DEMO_NOTE_ALT_DESCRIPTION));
    }

    @Test
    @Order(5)
    public void deleteExistingNoteAndVerifyItIsNoLongerListed() {
        WebDriverWait wait = new WebDriverWait(webDriver, 10);

        // Step 1: login the user
        webDriver.get(String.format("http://localhost:%s/login", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Login");

        WebElement usernameInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        usernameInputField.sendKeys(DEMO_USER_USERNAME);

        WebElement passwordInputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        passwordInputField.sendKeys(DEMO_USER_PASSWORD);

        WebElement loginButton = webDriver.findElement(By.id("buttonLogin"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).submit();

        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonLogout")));

        assertThat(webDriver.getTitle()).isEqualTo("Home");

        // Step 2: Edit the note
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;

        WebElement navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        List<WebElement> deleteButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("delete-note-button")));

        if (deleteButtons.size() > 0) {
            deleteButtons.get(0).click();

            WebElement successDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("divSuccess")));

            assertThat(webDriver.getTitle()).isEqualTo("Result");
        }

        // Step 3: Check that the note is no longer listed.
        webDriver.get(String.format("http://localhost:%s/dashboard", this.port));

        assertThat(webDriver.getTitle()).isEqualTo("Home");

        navNotesTab = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        executor.executeScript("arguments[0].click()", navNotesTab);

        assertThat(navNotesTab.getText().contains(DEMO_NOTE_ALT_TITLE)).isFalse();
        assertThat(navNotesTab.getText().contains(DEMO_NOTE_ALT_DESCRIPTION)).isFalse();
    }
}
