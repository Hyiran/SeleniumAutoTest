package com.framework.webdriver;


import com.framework.browser.*;
import com.framework.data.util.DataUtil;
import com.framework.data.util.ProccessHepler;
import com.framework.data.util.TestHelper;
import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.util.InitProperties;
import com.framework.util.Log;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RunTest extends DriverBase {
    public static Browser browser = null;
    public static Config config = new Config(Constants.config);
    public static Logger log = Log.getInstance();
    private static DataUtil data = DataUtil.getInstance();
    private static WebDriver driver = start();
    public static TestHelper helper = new TestHelper();

    public static Config getConfig() {
        return config;
    }

    public static WebDriver getDriver() {
        return driver;
    }

    @BeforeSuite//启动浏览器
    private static WebDriver start() {
        log.info("初始化系统参数配置");
        new InitProperties();//初始化系统配置文件
        if (driver == null) {
            if (Constants.CHROME.equalsIgnoreCase(config.get("BROWSER")))
                browser = new ChromeBorowser();
            else if (Constants.FireFox.equalsIgnoreCase(config.get("BROWSER")))
                browser = new FirefoxBrowser();
            else if (Constants.IE.equalsIgnoreCase(config.get("BROWSER")))
                browser = new IEBrowser();
            else if (Constants.JBrowser.equalsIgnoreCase(config.get("BROWSER")))
                browser = new JBrowser();
            else if (Constants.PhantomeBrowser.equalsIgnoreCase(config.get("BROWSER")))
                browser = new PhantomBrowser();
            String killBrowser = config.get("killBrowser");
            List<String> browserList = new ArrayList<String>();
            browserList.add(Constants.FirefoxName);
            browserList.add(Constants.IEName);
            browserList.add(Constants.ChromeName);
            browserList.add(Constants.JBrowserName);
            browserList.add(Constants.PhantomeBrowserName);
            if ((null != killBrowser) && (!"".equalsIgnoreCase(killBrowser))) {
                if (killBrowser.contains(",")) {
                    String[] strings = killBrowser.split(",");
                    for (String s : browserList) {
                        boolean killFlag = false;
                        for (String string : strings) {
                            if (s.equalsIgnoreCase(string)) {
                                killFlag = true;
                                break;
                            }
                        }
                        if (killFlag) {
                            ProccessHepler.CloseProcess(s);
                        }
                    }
                    killDriverServer();
                } else {
                    for (String s : browserList) {
                        if (s.equalsIgnoreCase(killBrowser)) {
                            ProccessHepler.CloseProcess(s);
                        }
                    }
                    killDriverServer();
                }
            } else {
                ProccessHepler.CloseProcess(Constants.ChromeName);
                ProccessHepler.CloseProcess(Constants.IEDriverName);
                ProccessHepler.CloseProcess(Constants.IEName);
                ProccessHepler.CloseProcess(Constants.ChromeDriverName);
                ProccessHepler.CloseProcess(Constants.FirefoxName);
                ProccessHepler.CloseProcess(Constants.PhantomeBrowserName);
                ProccessHepler.CloseProcess(Constants.JBrowserName);
            }
            String killCommon = config.get("killCommon");
            if ((null != killCommon) && (!"".equalsIgnoreCase(killCommon))) {
                if (killCommon.contains(",")) {
                    String[] strings = killCommon.split(",");
                    for (int i = 0; i < strings.length; i++)
                        ProccessHepler.CloseProcess(strings[i]);
                } else {
                    ProccessHepler.CloseProcess(killCommon);
                }
            }
        }
        if ((browser != null) && (config.get("BROWSER") != null)) {
            driver = browser.start();
        }
        log.info("浏览器启动成功");
        return driver;
    }

    private static void killDriverServer() {
        ProccessHepler.CloseProcess(Constants.IEDriverName);
        ProccessHepler.CloseProcess(Constants.ChromeDriverName);
    }

    @AfterSuite
    public static void ClosePage() {

        closeWebDriver(driver);
        if (Constants.CHROME.equalsIgnoreCase(config.get("BROWSER"))) {
            ProccessHepler.CloseProcess(Constants.ChromeName);
        } else if (Constants.FireFox.equalsIgnoreCase(config.get("BROWSER"))) {
            ProccessHepler.CloseProcess(Constants.ChromeDriverName);
        } else if (Constants.IE.equalsIgnoreCase(config.get("BROWSER"))) {
            ProccessHepler.CloseProcess(Constants.IEDriverName);
        }else if (Constants.JBrowser.equalsIgnoreCase(config.get("BROWSER"))) {
            ProccessHepler.CloseProcess(Constants.JBrowserName);
        }else if (Constants.PhantomeBrowser.equalsIgnoreCase(config.get("BROWSER"))) {
            ProccessHepler.CloseProcess(Constants.PhantomeBrowserName);
        }

    }

    @BeforeClass
    public void beforeClass() {
        initPage();

    }

    public void initPage() {

    }

    private static void closeWebDriver(WebDriver driver) {
        if (driver == null)
            return;
        try {
            String current = driver.getWindowHandle();
            Set<String> otherWins = driver.getWindowHandles();
            for (String winId : otherWins) {
                if (winId.equals(current)) {
                    continue;
                }
                driver.switchTo().window(winId).close();
            }
        } catch (Exception ex) {
        } finally {
            try {
                driver.quit();
            } catch (Exception ex) {
            }
        }
    }


}