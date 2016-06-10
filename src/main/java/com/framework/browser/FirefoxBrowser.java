package com.framework.browser;

import com.framework.listener.MyEventListener;
import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.webdriver.DriverBase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class FirefoxBrowser extends DriverBase implements Browser {
    WebDriver driver;
    public static Config config = new Config(Constants.config);

    public WebDriver start() {
        String driver_dir = null;
        if (!config.get("BrowserLocation").equals(""))
            driver_dir = config.get("BrowserLocation");
        else {
            driver_dir = Constants.FirefoxPath;
        }
        System.setProperty(Constants.FirefoxProperty, driver_dir);
        ProfilesIni allProfiles = new ProfilesIni();
        FirefoxProfile profile = new FirefoxProfile();
        String profileName = System.getProperty("ProfileName", "default");
        String profilePath = config.get("ProfilePath");
        if (!profileName.isEmpty()) {
            profile = allProfiles.getProfile(profileName);
        } else if (!profilePath.isEmpty()) {
            File profileDir = new File(profilePath);
            profile = new FirefoxProfile(profileDir);
        }
        this.driver = new FirefoxDriver(profile);
        this.driver.manage().timeouts().implicitlyWait(Constants.TimeOut, TimeUnit.SECONDS);
        this.driver.manage().window().maximize();
        EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
        eventFiringWebDriver.register(new MyEventListener());
        driver=eventFiringWebDriver;
        return this.driver;


    }
}