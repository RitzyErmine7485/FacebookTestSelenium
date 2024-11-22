package com.mayab.quality.functional;

import org.junit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

public class FacebookTest {
    private WebDriver driver;
    private StringBuilder errors = new StringBuilder();
    JavascriptExecutor js;

    @Before
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        js = (JavascriptExecutor) driver;
    }

    @Test
    public void facebookFail() throws Exception {
        driver.get("https://www.facebook.com/");
        
        driver.findElement(By.id("email")).clear();
        driver.findElement(By.id("email")).sendKeys("TeddyRichtoffen");
        
        driver.findElement(By.id("pass")).clear();
        driver.findElement(By.id("pass")).sendKeys("IsALiar"); // I'm not, but that's why no one would guess it!
        
        driver.findElement(By.name("login")).click();
        
        // Mi Chrome está en inglés        
        String result = driver.findElement(By.xpath("//div[contains(text(), 'The email or mobile number')]")).getText();
        assertThat(result, is("The email or mobile number you entered isn’t connected to an account. Find your account and log in."));
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        if (errors.length() > 0) {
            fail(errors.toString());
        }
    }
}
