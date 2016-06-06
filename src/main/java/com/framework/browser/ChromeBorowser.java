package com.framework.browser;

import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.util.Log;
import com.framework.webdriver.DriverBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

/**
 * Created by caijianmin on 2016/5/25.
 */
public class ChromeBorowser extends DriverBase implements Browser {
    public static Config config = new Config(Constants.config);
    public static Logger log = Log.getInstance();
    WebDriver driver;
    @Override
    public WebDriver start() {
        String driver_dir = null;
        if (!config.get("BrowserLocation").equals(""))
            driver_dir = config.get("BrowserLocation");
        else {
            driver_dir = Constants.ChromeDriverPath;
        }
        System.setProperty(Constants.ChromeProperty, driver_dir);

        String chrome_user_data_dir = config.get("CHROME_USER_DATA_DIR");
        if (!chrome_user_data_dir.equals("")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(new String[]{"user-data-dir=" + chrome_user_data_dir});
            this.driver = new ChromeDriver(options);
            log.info("\n设置Webdriver启动chrome为默认用户的配置信息（包括书签、扩展程序等）\n路径为：[" + chrome_user_data_dir + "]");
        } else {
            this.driver = new ChromeDriver();
            log.info("\n设置Webdriver启动chrome为新用户，即默认，不做配置。");
        }

        this.driver.manage().timeouts().implicitlyWait(Constants.TimeOut, TimeUnit.SECONDS);
        this.driver.manage().window().maximize();
        return driver;
    }
}
