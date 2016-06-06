package com.framework.driver;

import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.webdriver.DriverBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

public class IEBrowser extends DriverBase implements Browser {
    WebDriver driver;
    public static Config config = new Config(Constants.config);

    public WebDriver start() {
        String driver_dir = null;
        if (!config.get("BrowserLocation").equals(""))
            driver_dir = config.get("BrowserLocation");
        else {
            driver_dir = Constants.IEDriverPath;
        }
        System.setProperty(Constants.IEProperty, driver_dir);

        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability("ignoreProtectedModeSettings", true);
        this.driver = new InternetExplorerDriver(ieCapabilities);
        this.driver.manage().timeouts().implicitlyWait(Constants.TimeOut, TimeUnit.SECONDS);
        this.driver.manage().window().maximize();
        return this.driver;
    }
}