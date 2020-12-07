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
}
