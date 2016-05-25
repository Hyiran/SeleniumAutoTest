package com.framework.webdriver;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.jd.pop.qa.data.TestHelper;
import com.jd.pop.qa.util.ExcelReport;
import com.machinepublishers.jbrowserdriver.Element;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.test.util.qa.util.Base;
import com.test.util.qa.util.InitProperties;
import com.test.util.qa.util.Logger;
import com.test.util.qa.util.ProccessHepler;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jd.pop.qa.util.ToolKit.sendGet;

public class DriverBase extends Base {
    private WebDriver dr = null;
    protected static TestHelper helper = new TestHelper();
    public static WebDriverPlus driver = null;
    public static int implicitlyWait = Integer.parseInt(System.getProperty("WebDriver.ImplicitlyWait"));
    public static int webDriverWait = Integer.parseInt(System.getProperty("WebDriver.WebDriverWait"));
    public String browser = System.getProperty("WebDriver.Browser");//获取运行的浏览器
    private static ExcelReport reporter = ExcelReport.getInstance();
    public static String senario = "默认场景";
    public static String testcase = "默认用例";
    public static String desc = "默认描述";
    public static String picPath = " ";
    public final static String pass = "Pass";
    public final static String fail = "Fail";
    public void launch() {
        String killBrowser = System.getProperty("killBrowser");
        List<String> browserList = new ArrayList<String>();
        browserList.add("firefox.exe");
        browserList.add("iexplore.exe");
        browserList.add("chrome.exe");
        if ((null != killBrowser) && (!"".equalsIgnoreCase(killBrowser))) {
            if (killBrowser.contains(",")) {
                String[] strings = killBrowser.split(",");
                for (String s : browserList) {
                    boolean killFlag = false;
                    for (int k = 0; k < strings.length; k++) {
                        if (s.equalsIgnoreCase(strings[k])) {
                            killFlag = true;
                            break;
                        }
                    }
                    if (killFlag) {
                        Logger.log("杀死浏览器" + s);
                        ProccessHepler.CloseProcess(s);
                    }
                }
            } else {
                Logger.log("杀死浏览器" + killBrowser);
                ProccessHepler.CloseProcess(killBrowser);
            }
        }
        String browserLocation = System.getProperty("WebDriver.Browser.Location");

        if (browser.equalsIgnoreCase("Firefox")) {
            if ((browserLocation != null) && (!InitProperties.MATRIXFLAG)) {
                System.setProperty("webdriver.firefox.bin", browserLocation);
            }
            Logger.log("打开Firefox浏览器");
            ProfilesIni allProfiles = new ProfilesIni();
            FirefoxProfile profile = new FirefoxProfile();
            String profileName = System.getProperty("ProfileName", "default");
            String profilePath = System.getProperty("ProfilePath");
            if (!profileName.isEmpty()) {
                profile = allProfiles.getProfile(profileName);
            } else if (!profilePath.isEmpty()) {
                File profileDir = new File(profilePath);
                profile = new FirefoxProfile(profileDir);
            }
            this.dr = new FirefoxDriver(profile);
        } else if (browser.equalsIgnoreCase("Chrome")) {
            System.setProperty("webdriver.chrome.driver", browserLocation);
            ChromeOptions options = new ChromeOptions();
            String userDataDir = System.getProperty("Chrome.USER-DATA-DIR", ("C:/Users/" + System.getProperty("user.name") + "/AppData/Local/Google/Chrome/User Data").replace("/", File.separator));
            String arg = "user-data-dir=" + userDataDir;
            options.addArguments(new String[]{arg});
            options.addArguments(new String[]{"test-type"});
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability("chromeOptions", options);
            Logger.log("打开Chrome浏览器");
            this.dr = new ChromeDriver(capabilities);
        } else if(browser.equalsIgnoreCase("IE")) {
            System.setProperty("webdriver.ie.driver", browserLocation);
            DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability("ignoreProtectedModeSettings", true);
            Logger.log("打开IE浏览器");
            this.dr = new InternetExplorerDriver(capabilities);
        } else if(browser.equalsIgnoreCase("HtmlUnitDriver")){
            Logger.log("进入不打开浏览器模式");
            this.dr = new HtmlUnitDriver(true);
        } else if(browser.equalsIgnoreCase("PhantomJs")){
            Logger.log("打开PhantomJS浏览器");
            Capabilities caps = new DesiredCapabilities();
            ((DesiredCapabilities) caps).setJavascriptEnabled(true);
            ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
            if (!"".equals(browserLocation)&&null!=browserLocation) {
                ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, browserLocation);
            }
            this.dr = new PhantomJSDriver(caps);
        }else if(browser.equalsIgnoreCase("JBrowserDriver")) {
            Logger.log("打开JBrowserDriver浏览器");
            Settings.Builder builder= new Settings.Builder();
            builder.blockAds(true);//是否过滤广告
            builder.javascript(true);//是否支持JS
           // builder.userAgent(UserAgent.CHROME);
           // builder.requestHeaders(RequestHeaders.CHROME);
            builder.screen(new Dimension(1920, 1080));
            builder.headless("false".equalsIgnoreCase(System.getProperty("OpenBrowser","true")));//是否打开浏览器
            builder.quickRender("false".equalsIgnoreCase(System.getProperty("LoadImg", "true")));//配置是否加载图片
            builder.buildCapabilities();
            this.dr = new JBrowserDriver(builder.build());
        }
        driver = new WebDriverPlus(this.dr);
        Logger.log("设置全局显示等待时间");
        implicitlyWait = Integer.parseInt(System.getProperty("WebDriver.ImplicitlyWait").trim());
        webDriverWait = Integer.parseInt(System.getProperty("WebDriver.WebDriverWait").trim());
        driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
        driver.manage().window().maximize();
    }

    /**
     * 得到当前页面的URL
     * 2015/12/25
     */
    public static String getCurrentPageURL() {
        String pageUrl = "";
        JavascriptExecutor je = (JavascriptExecutor) driver;
        final String docstate = (String) je.executeScript("return document.readyState");
        Logger.Defaultlog("Current loading page state is:" + docstate);
        WebDriverWait wait = new WebDriverWait(driver, webDriverWait);
        ExpectedCondition<Boolean> ec = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return (docstate.equals("complete"));
            }
        };
        Logger.Defaultlog("We just wait for the current page load correctly now...");
        wait.until(ec);
        pageUrl = driver.getCurrentUrl();
        return pageUrl;
    }
    /**执行PhantomJS脚本*/
    public Object executePhantomJS(String script,Object... args){
        try {
            if ("PhantomJs".equalsIgnoreCase(browser)) {
                return ((PhantomJSDriver)dr).executePhantomJS(script,args);
            }
        } catch (Exception e) {
            System.err.println("当前浏览器驱动不支持执行phantomJs");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 等待页面完全加载
     */
    public static void waitForPageLoad() {
        new WebDriverWait(driver, DriverBase.webDriverWait).until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState", new Object[0]).equals("complete");
            }
        });
    }

    /**
     * 根据窗口标题切换窗口
     */
    public static boolean switchToWindow(String windowTitle) {
        boolean flag = false;
        try {
            String currentHandle = driver.getWindowHandle();
            Set<String> handles = driver.getWindowHandles();
            for (String s : handles) {
                if (s.equals(currentHandle)) {
                    continue;
                }
                driver.switchTo().window(s);
                if (driver.getTitle().contains(windowTitle)) {
                    flag = true;
                    Logger.log("Switch to window: " + windowTitle + " successfully!");
                }
            }
        } catch (NoSuchWindowException e) {
            Logger.error("Window: " + windowTitle + " cound not found!" + e.fillInStackTrace());
            flag = false;
        }
        return flag;
    }

    /**根据PageSource关键字切换窗口*/
    public static void windowSwichToByWord(String keyword){
        Set<String> windowsHandles = driver.getWindowHandles();
        for (String handle:windowsHandles){
            if (driver.switchTo().window(handle).getPageSource().contains(keyword)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }
    /**
     * 更改Cookie
     */
    public static void editCookie(String cookieName, String cookieValue) {
        Cookie cooke = driver.manage().getCookieNamed(cookieName);
        Logger.log(String.format("更改前cookie:%s -> %s -> %s -> %s -> %s",
                cooke.getDomain(), cooke.getName(), cooke.getValue(), cooke.getExpiry(), cooke.getPath()));
        driver.manage().deleteCookieNamed(cookieName);
        Cookie c = new Cookie(cookieName, cookieValue, cooke.getDomain(), cooke.getPath(), cooke.getExpiry());
        driver.manage().addCookie(c);
        Cookie cooke1 = driver.manage().getCookieNamed(cookieName);
        Logger.log(String.format("更改后cookie:%s -> %s -> %s -> %s -> %s", cooke1.getDomain(), cooke1.getName(), cooke1.getValue(), cooke1.getExpiry(), cooke1.getPath()));
        driver.navigate().refresh();
    }

    /**
     * 粘贴操作
     */
    public static void paste(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);
        try {
            Robot rob = new Robot();
            rob.keyPress(17);
            rob.keyPress(86);
            rob.keyRelease(17);
            rob.keyRelease(86);
        } catch (AWTException localAWTException) {
        }
    }

    public static void pressKey(int key) {
        try {
            Robot rob = new Robot();
            rob.keyPress(key);
            rob.keyRelease(key);
        } catch (AWTException localAWTException) {
        }
    }

    public static void delay(int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Element getElementByTagAndName(String tag, String name) {
        Element element = new Element();
        element.locator = ("//" + tag + "[contains(text(),'" + name + "')]");
        element.type = "xpath";
        return element;
    }

    /**
     * 获取随机数
     */
    public static String getRandomData(String type, int length) {
        String data = "";
        String negativeData = "";
        String[] charData = {"!", "@", "#", "$", "%", "^", "&", "*"};
        if (type.equals("int")) {
            for (int i = 0; i < length - 1; i++) {
                data = data + (int) (10.0D * Math.random());
            }
            data = (int) (9.0D * Math.random() + 1.0D) + data;
        } else if (type.equals("nint")) {
            for (int i = 0; i < length - 1; i++) {
                data = data + (int) (10.0D * Math.random());
            }
            data = "-" + (int) (9.0D * Math.random() + 1.0D) + data;
        } else if (type.equals("double")) {
            for (int i = 0; i < length - 3; i++) {
                data = data + (int) (10.0D * Math.random());
            }
            for (int i = 0; i < 2; i++) {
                negativeData = negativeData + (int) (10.0D * Math.random());
            }
            data = (int) (9.0D * Math.random() + 1.0D) + data + "." + negativeData;
        } else if (type.equals("ndouble")) {
            for (int i = 0; i < length - 3; i++) {
                data = data + (int) (10.0D * Math.random());
            }
            for (int i = 0; i < 2; i++) {
                negativeData = negativeData + (int) (10.0D * Math.random());
            }
            data = "-" + (int) (9.0D * Math.random() + 1.0D) + data + "." + negativeData;
        } else if (type.equals("char")) {
            for (int i = 0; i < length; i++)
                data = data + String.valueOf((char) (97 + (int) (Math.random() * 26.0D)));
        } else if (type.equals("uchar")) {
            for (int i = 0; i < length; i++) {
                Random rnd = new Random();
                data = data + charData[rnd.nextInt(8)];
            }
        } else if (type.equals("china")) {
            for (int i = 0; i < length; i++) {
                data = data + "中";
            }
        }
        return data;
    }
    /**接口返回一个parm参数的参数值
     * url:接口的url
     * code：编码格式
     * rule：json串截取规则
     * parm：json串中需要的参数
     * */
    public static String interfaceCommon(String url,String code,String rule,String parm){
        String response = "";
        String parm_text = "";
        try {
            response = sendGet(url, code);
            Pattern p = Pattern.compile(rule);// 查找规则公式中大括号以内的字符
            Matcher m = p.matcher(response);
            if (m.find()) {
                response = m.group();
                JSONObject jsonObject = JSONObject.parseObject(response);
                parm_text = jsonObject.getString(parm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parm_text;
    }
    /**
     * 打印检查点到excel报告
     */
    public static void printCheckPoint(String status, String description, String remark) {
        String picPath = "";
        if ("Pass".equalsIgnoreCase(status)) {
            reporter.Add2Report(senario, testcase, desc, status, description, remark, picPath);
        } else if ("Fail".equalsIgnoreCase(status))
            reporter.Add2Report(senario, testcase, desc, status, description, remark, picPath);
    }

    public static void printCheckPoint(String expert, String actual) {
        String status = expert.equalsIgnoreCase(actual) ? "Pass" : "Fail";
        String resultDes = "期望结果与实际结果一致";
        if (status == "Fail") {
            resultDes = "期望结果与实际结果不一致";
        }
        if (expert.equalsIgnoreCase(actual))
            reporter.Add2Report(senario, testcase, resultDes, "Pass", "期望结果与实际结果一致", "期望结果:" + expert + "  <br/>  实际结果:" + actual, picPath);
        else
            reporter.Add2Report(senario, testcase, resultDes, "Fail", "期望结果与实际结果不一致", "期望结果:" + expert + "   <br/> 实际结果:" + actual, picPath);
    }
}