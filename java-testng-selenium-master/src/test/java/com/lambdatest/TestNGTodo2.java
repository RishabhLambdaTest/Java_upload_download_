package com.lambdatest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestNGTodo2 {
    String username = System.getenv("LT_USERNAME") == null ? "Your LT Username" : System.getenv("LT_USERNAME");
    String authkey = System.getenv("LT_ACCESS_KEY") == null ? "Your LT AccessKey" : System.getenv("LT_ACCESS_KEY");

    public RemoteWebDriver driver;
    public String gridURL = "@hub.lambdatest.com/wd/hub";
    String status = "passed";

    @BeforeTest
    public void setUp() throws Exception {
        ChromeOptions options = new ChromeOptions();
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "chrome");
        capabilities.setCapability("version", "109");
        capabilities.setCapability("build", "Download functionality test");
        capabilities.setCapability("name", "sample test");
        capabilities.setCapability("network", true); // To enable network logs
        capabilities.setCapability("visual", true);
        capabilities.setCapability("video", true); // To enable video recording`
        capabilities.setCapability("console", true); // To capture console logs
        capabilities.merge(options);

        try {

            driver = new RemoteWebDriver(new URL("https://" + username + ":" + authkey + gridURL), capabilities);

        } catch (MalformedURLException e) {
            System.out.println("Invalid grid URL");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test()
    public void fileDownload() throws Exception {
        try {

            driver.get("https://www.stats.govt.nz/large-datasets/csv-files-for-download/");
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            WebElement element = driver.findElement(
                    By.xpath("/html/body/div[12]/div/div/main/section/div/div/div/article/div/div[2]/article/ul/li[5]/div/div/h3/a"));
            element.click();

            Thread.sleep(5000);


////////////Validation for downloaded file on Lambda Device//////////////
            Assert.assertEquals(((JavascriptExecutor) driver).executeScript("lambda-file-exists=business-operations-survey-2022-business-finance.csv"),true); // file exist check
            System.out.println(((JavascriptExecutor) driver).executeScript("lambda-file-stats=business-operations-survey-2022-business-finance.csv")); // retrieve file stats

            String base64EncodedFile = ((JavascriptExecutor) driver)
                    .executeScript("lambda-file-content=business-operations-survey-2022-business-finance.csv").toString(); // file content download
//             System.out.println(base64EncodedFile);

            byte[] data = Base64.getDecoder().decode(base64EncodedFile);
            File file = new File("File Folder//Download Folder//business-operations-survey-2022-business-finance.csv");
            FileOutputStream fos = new FileOutputStream(file);
            String fileContent = readFileContent("File Folder//Download Folder//business-operations-survey-2022-business-finance.csv");
//            System.out.println("File content:\n" + fileContent);
            fos.write(data);

/////////////Code Snippet for File deletion//////////////////////
            Thread.sleep(10000);
            File filename = new File("File Folder//Download Folder//business-operations-survey-2022-business-finance.csv");
            if(filename.delete())
            System.out.println("file deleted");

        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());

            SessionId id = driver.getSessionId();
            System.out.println("Failed test session id: " + id.toString());
        }

    }

    private String readFileContent(String string) {
        return null;
    }

    @AfterTest
    public void tearDown() throws Exception {
        if (driver != null) {
            ((JavascriptExecutor) driver).executeScript("lambda-status=" + status);
            driver.quit();
        }
    }
}
