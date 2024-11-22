package com.mayab.quality.functional;

import org.junit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;

public class CRUDSeleniumTest {
	private WebDriver driver;
    private StringBuilder errors = new StringBuilder();
    private WebDriverWait wait;
    JavascriptExecutor js;

    @Before
    public void setUp() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
        js = (JavascriptExecutor) driver;
    }
    
    public void createUser() throws Exception {
    	driver.get("https://mern-crud-mpfr.onrender.com/");

        driver.findElement(By.xpath("//button[text()='Add New']")).click();

        driver.findElement(By.name("name")).sendKeys("Pacotest");
        driver.findElement(By.name("email")).sendKeys("paco@test.com");
        driver.findElement(By.name("age")).sendKeys("20");
        driver.findElement(By.xpath("//div[contains(@class, 'ui selection dropdown')]")).click();
        driver.findElement(By.xpath("//div[@role='option']/span[text()='Male']")).click();

        driver.findElement(By.xpath("//button[text()='Add']")).click();
    }
    
    public void deleteUser() {
    	driver.get("https://mern-crud-mpfr.onrender.com/");
    	
    	driver.findElement(By.xpath("//td[text()='Pacotest']/following-sibling::td/button[contains(@class, 'black') and contains(@class, 'button')]")).click();
    	
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@class, 'red') and contains(@class, 'button') and @data-userid]")));
    	
    	driver.findElement(By.xpath("//button[contains(@class, 'red') and contains(@class, 'button') and @data-userid]")).click();
    }
    
	public boolean findUser() {
		List<WebElement> rows = driver.findElements(By.xpath("//table[@class='ui single line table']//tbody//tr"));
        boolean userFound = true;
        
        for (WebElement row : rows) {
            String name = row.findElement(By.xpath(".//td[1]")).getText();
            if (name.equals("Pacotest")) {
            	userFound = true;
                break;
            }
        }
        
        return userFound;
	}
    
    @Test
    // Create: Success
    public void test1() throws Exception {
        createUser();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'ui green success message')]//p")));
        
        WebElement successMessage = driver.findElement(By.xpath("//div[contains(@class, 'ui green success message')]//p"));
        assertThat(successMessage.getText(), is("Successfully added!"));
    }

    @Test
    // Create: Duplicate
    public void test2() throws Exception {
    	createUser();
    	
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'ui yellow warning message')]//p")));
        
        WebElement successMessage = driver.findElement(By.xpath("//div[contains(@class, 'ui yellow warning message')]//p"));
        assertThat(successMessage.getText(), is("That email is already taken."));
    }
    
    @Test
    // Update: Age
    public void test3() throws Exception {
    	driver.get("https://mern-crud-mpfr.onrender.com/");
    	
    	driver.findElement(By.xpath("//td[text()='Pacotest']/following-sibling::td/button[contains(@class, 'blue') and contains(@class, 'button')]")).click();
    	
        wait.until(ExpectedConditions.attributeToBe(driver.findElement(By.name("age")), "value", "20"));
        
    	WebElement ageField = driver.findElement(By.name("age"));
    	ageField.clear();
    	ageField.sendKeys("22"); 
     
    	driver.findElement(By.xpath("//button[text()='Save']")).click();
    	
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'ui green success message')]//p")));
    	 
    	WebElement successMessage = driver.findElement(By.xpath("//div[contains(@class, 'ui green success message')]//p"));
        assertThat(successMessage.getText(), is("Successfully updated!"));
    }
    
    @Test
    // Delete
    public void test4() throws Exception {
    	deleteUser();

    	Thread.sleep(1000);

    	List<WebElement> rows = driver.findElements(By.xpath("//table[@class='ui single line table']//tbody//tr"));
        boolean userNotFound = true;
        
        for (WebElement row : rows) {
            String name = row.findElement(By.xpath(".//td[1]")).getText();
            if (name.equals("Pacotest")) {
            	userNotFound = false;
                break;
            }
        }
        
        assertThat(userNotFound, is(true)); 
    }
    
    @Test
    // Search: Name
    public void test5() throws Exception {
    	driver.get("https://mern-crud-mpfr.onrender.com/");

        createUser();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'ui green success message')]//p")));
        
        boolean userFound = findUser();
        
        deleteUser();
        
        assertThat(userFound, is(true));
    }
    
    @Test
    // Search: All
    public void test6() throws Exception {
    	driver.get("https://mern-crud-mpfr.onrender.com/");
        
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='ui single line table']")));
        
        WebElement table = driver.findElement(By.xpath("//table[@class='ui single line table']"));
        assertThat(table.isDisplayed(), is(true));
    }
    
    @After
    public void tearDown() throws Exception {
        driver.quit();
        if (errors.length() > 0) {
            fail(errors.toString());
        }
    }
}
