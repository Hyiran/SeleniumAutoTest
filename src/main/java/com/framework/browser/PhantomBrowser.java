package com.framework.browser;

import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.util.Log;
import com.framework.webdriver.DriverBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by caijianmin on 2016/5/25.
 */
public class PhantomBrowser extends DriverBase implements Browser {
    WebDriver driver;
    public static Logger log = Log.getInstance();
    public static Config config = new Config(Constants.config);
    @Override
    public WebDriver start() {
        log.info("打开PhantomJS浏览器");
        String browserLocation= config.get("BrowserLocation");
        Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
        if (!"".equals(browserLocation)&&null!=browserLocation) {
            ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, browserLocation);
        }
       return this.driver = new PhantomJSDriver(caps);
    }
}
