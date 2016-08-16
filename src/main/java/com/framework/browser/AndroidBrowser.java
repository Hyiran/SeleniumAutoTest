package com.framework.browser;

import com.framework.webdriver.DriverBase;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by caijianmin on 8/15 0015.
 */
public class AndroidBrowser extends DriverBase implements IBrowser {

    private static AppiumDriver appiumDriver;

    @Override
    public WebDriver start() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        // 设置参数
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, System.getProperty("Appium.PlatformName"));
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, System.getProperty("Appium.PlatformVersion"));
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, System.getProperty("Appium.DeviceName"));
        // 配置中的app名称必须与实际安装的app名称一致
        capabilities.setCapability(MobileCapabilityType.APP, System.getProperty("user.dir") + "/" + System.getProperty("Appium.App"));
        capabilities.setCapability("appPackage", System.getProperty("Appium.AppPackage"));
        capabilities.setCapability("appActivity", System.getProperty("Appium.AppActivity"));
//        capabilities.setCapability(MobileCapabilityType.APP_WAIT_ACTIVITY, System.getProperty("Appium.appWaitActivity"));
//        capabilities.setCapability(MobileCapabilityType.APP_WAIT_PACKAGE, System.getProperty("com.jingdong.app.mall"));
        capabilities.setCapability("autoLaunch", System.getProperty("false"));
        capabilities.setCapability("noSign", System.getProperty("Appium.noSign"));
        capabilities.setCapability("unicodeKeyboard", "true");
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, "600");
        // 不重新安装app，appium安装导致app提示恶意篡改
        capabilities.setCapability("noReset", true);
        capabilities.setCapability("fullReset", false);
        System.out.println(capabilities);
        // capabilities.setCapability("udid", getUdid());
        try {
            appiumDriver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  appiumDriver;
    }
}
