package se.jepp.sas;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class FrontendTest {
    private ChromeDriver driver;
    private JavascriptExecutor js;

    @BeforeClass
    public void setUpClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        js = (JavascriptExecutor) driver;
        driver.get("https://www.sas.se/");
        driver.manage().window().maximize();
        Cookie cookie = new Cookie("_cookienew", "acknowledged");
        driver.manage().addCookie(cookie);
        driver.get("https://www.sas.se/");
    }
    @AfterMethod
    public void tearDown(){
        driver.manage().deleteAllCookies();
        driver.close();
    }

    @Test(groups = "Login")
    public void shouldTryToLoginWithInvalidDetails() throws InterruptedException {
        driver.findElement(By.xpath("/html/body/s4s-main-header/header/div/button/span")).click();
        Thread.sleep(1000);
        Assert.assertTrue(driver.findElement(By.xpath("/html/body/s4s-login/div[1]")).isEnabled());
        Thread.sleep(1000);
        driver.findElement(By.name("email-input")).sendKeys("jofey10599@procowork.com");
        driver.findElement(By.name("password-input")).click();
        driver.findElement(By.name("password-input")).sendKeys("test1234");
        driver.findElement(By.id("login-button")).click();
        Assert.assertTrue(driver.findElement(By.xpath("/html/body/s4s-login/div[1]/div/form/div[2]/s4s-input[2]/span")).isEnabled()); //checks if red banner after failed search is active
    }

    @Test
    public void shouldNavigateToSakerhetskontroll() throws InterruptedException {
        driver.findElement(By.xpath("/html/body/s4s-main-header/header/div/div/nav/ul/li[5]/a")).click();
        Thread.sleep(1000);
        Assert.assertTrue(driver.findElement(By.xpath("/html/body/s4s-main-header/header/div/div/nav/ul/li[5]/ul")).isEnabled());
        driver.findElement(By.xpath("/html/body/s4s-main-header/header/div/div/nav/ul/li[5]/ul/li[2]/ul/li[2]")).click();
        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().equalsIgnoreCase("https://www.sas.se/reseinfo/pa-flygplatsen/sakerhetskontroll-fast-track/"));
    }

    @Test(groups = "Booking")
    public void invalidBookingId() throws InterruptedException {
        driver.findElement(By.xpath("/html/body/s4s-main-header/header/div/div/nav/ul/li[4]")).click();
        Assert.assertTrue(driver.getCurrentUrl().equalsIgnoreCase("https://www.sas.se/managemybooking/"));
        driver.findElement(By.xpath("//*[@id=\"Name\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"Name\"]")).sendKeys("Test12");
        driver.findElement(By.xpath("//*[@id=\"lastName\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"lastName\"]")).sendKeys("Test Test");
        driver.findElement(By.xpath("//*[@id=\"CheckinSearchBtn\"]")).click();
        Thread.sleep(2000);
        Assert.assertTrue(driver.findElement(By.xpath("//*[@id=\"notificationMessage\"]")).isEnabled()); //checks if red banner after failed search is active
    }

    @Test(groups = "Booking")
    public void shouldCancelBookingBeforePayment() throws InterruptedException {
        driver.findElement(By.xpath("//*[@id='cep-origin-input']")).click();
        driver.findElement(By.xpath("//*[@id='cep-origin-input']")).sendKeys("arlanda");
        driver.findElement(By.xpath("//*[@id='cep-origin-input']")).sendKeys(Keys.ENTER);
        driver.findElement(By.xpath("//*[@id='cep-destination-input']")).click();
        driver.findElement(By.xpath("//*[@id='cep-destination-input']")).sendKeys("london");
        driver.findElement(By.xpath("//*[@id='cep-destination-input']")).sendKeys(Keys.ENTER);
        driver.findElement(By.xpath("//*[@id=\"ow\"]")).click();
        driver.findElement(By.xpath("//input[@id='outbound-date-input']")).click();
        Thread.sleep(1000);
        Assert.assertTrue( driver.findElement(By.xpath("//*[@id='cep-datepicker']/div[2]")).isEnabled());
        driver.findElement(By.xpath("//*[@id='cep-datepicker']/div[2]/div[2]/s4s-calendar[1]/table/tbody/tr[3]/td[7]/button")).click();
        driver.findElement(By.xpath("//*[@id='cep-datepicker']/div[2]/div[3]/button[2]")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id='cep-search']")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("https://www.sas.se/book/flights/?search"));
    }

    @Test
    public void shouldRedirectToExternalBookingPage() throws InterruptedException {
        js.executeScript("window.scrollTo(0,3000)");
        Thread.sleep(1000);
        driver.findElement(By.linkText("Temaresor")).click();
        Thread.sleep(1000);
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.sas.se/temaresor/");
        driver.findElement(By.xpath("//*[@id=\"template\"]/div/div/section/button[3]")).click();
        driver.findElement(By.cssSelector("#content > div > div > div > div > div > div:nth-child(3) > div > ul > li:nth-child(1) > div > div > picture > h3 > a")).click();
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.sas.se/temaresor/weekendresor/");
        js.executeScript("window.scrollTo(0,1000)");
        driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div[6]/div/div/div[7]/div/div[2]/div/button")).click();
        ArrayList<String> newTab = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(newTab.get(1));
        Thread.sleep(1000);
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.apollo.se/");
    }
}
