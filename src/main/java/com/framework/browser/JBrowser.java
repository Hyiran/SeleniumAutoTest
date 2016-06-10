package com.framework.browser;

import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.webdriver.DriverBase;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.RequestHeaders;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.UserAgent;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

/**
 * Created by caijianmin on 2016/5/25.
 */
public class JBrowser extends DriverBase implements Browser {
    WebDriver driver;
    public static Config config = new Config(Constants.config);
    @Override
    public WebDriver start() {
        Settings.Builder builder= new Settings.Builder();
        builder.blockAds(true);//是否过滤广告
        builder.javascript(true);//是否支持JS
        builder.userAgent(UserAgent.CHROME);
        builder.requestHeaders(RequestHeaders.CHROME);
        builder.screen(new Dimension(1920, 1080));
        builder.headless("false".equalsIgnoreCase(config.get("OpenBrowser")));//是否打开浏览器
        builder.quickRender("false".equalsIgnoreCase(config.get("LoadImg")));//配置是否加载图片
        builder.buildCapabilities();
        return this.driver = new JBrowserDriver(builder.build());
    }
}
