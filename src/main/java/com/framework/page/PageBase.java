package com.framework.page;

import com.framework.data.util.Element;
import com.framework.report.ExcelReport;
import com.framework.webdriver.RunTest;
import com.google.common.base.Function;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caijianmin on 2016/6/6.
 */
public abstract class PageBase implements Page {

    static WebDriver driver =RunTest.getDriver();
    

    private static ExcelReport reporter = ExcelReport.getInstance();
    public static String senario = "默认场景";
    public static String testcase = "默认用例";
    public static String desc = "默认描述";
    public static String picPath = " ";
    public final static String pass = "Pass";
    public final static String fail = "Fail";
    

    @Override
    public abstract void init(HashMap<String, Object> paramHashMap) ;

    /**
     * 截取整个浏览器页面
     */
    public static void screenShot(String name) {
        String path = System.getProperty("user.dir") + "/test-output/screenshot";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        File screenShotFile =  ((TakesScreenshot) RunTest.getDriver()).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenShotFile, new File(path + "/" + name + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 只接当前屏幕图片
     */
    public static String takeScreenshot( String name) {
        String path = System.getProperty("user.dir") + "/test-output/screenshot";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            path = path + "/" + name+ ".jpg";
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage screenshot = new Robot().createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));
            File f = new File(path);

            ImageIO.write(screenshot, "jpg", f);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return path;
    }

    public void screenShot() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        screenShot(df.format(new Date()));
        takeScreenshot(df.format(new Date()));
    }



    /**
     * 发送GET请求
     */
    public static String sendGet(String url, String code) throws Exception {
//        BufferedReader in = null;
        String content = null;
        // 定义HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
        try {
            // 实例化HTTP方法
            HttpGet request = new HttpGet(url);
            request.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(request);
            int status=response.getStatusLine().getStatusCode();//获取返回状态码
            System.out.println("状态码"+status);
            if (status!=200) {
                throw new Exception("接口不可访问!"+"状态码:"+status);
            }
            HttpEntity httpEntity = response.getEntity();
            content= EntityUtils.toString(httpEntity, code);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送Get请求报错！:" + url);
            httpClient.close();
        } finally {
            try {
                if (httpClient!=null) {
                    httpClient.close();
                }
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }
    public static int getRequestStatus(String url) throws Exception {
        int status=0 ;
        // 定义HttpClient
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
        try {
            // 实例化HTTP方法
            HttpGet request = new HttpGet(url);
            request.setConfig(requestConfig);
            HttpResponse response = httpClient.execute(request);
            status=response.getStatusLine().getStatusCode();//获取返回状态码
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送Get请求报错！:" + url);
            httpClient.close();
        } finally {
            try {
                if (httpClient!=null) {
                    httpClient.close();
                }
                return status;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    /***/
    public static String regMatch(String text,String reg){
        Pattern p = Pattern.compile(reg);// 查找规则公式中大括号以内的字符
        Matcher m = p.matcher(text);
        if (m.find()) {// 遍历找到的所有大括号
            return  m.group();
        }else {
            return null;
        }
    }

    /**
     * 发送POST请求
     */
    public Map sendHttpPostRequest(String url, Map<String, String> map) {
        Map returnMap = new HashMap();
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        java.util.List<NameValuePair> param = new ArrayList<NameValuePair>();
        for (String s : map.keySet()) {
            param.add(new BasicNameValuePair(s, map.get(s)));
        }
        String reponseText = "";
        try {
            post.setEntity(new UrlEncodedFormEntity(param));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            reponseText = EntityUtils.toString(response.getEntity());
            returnMap.put("status", response.getStatusLine().getStatusCode());
            returnMap.put("reponseText", reponseText);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return returnMap;
    }

    /**
     * 根据正则提取字符串
     */
    public String extratcByReg(String str, String reg) throws Exception {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        while (!m.find()) {
            throw new Exception("没有找到匹配字符串");
        }
        return m.group();
    }


    /**
     * 等待页面完全加载
     */
    public static void waitForPageLoad() {
        new WebDriverWait(driver, 30L).until(new Function<WebDriver, Boolean>() {
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
                    System.out.println("Switch to window: " + windowTitle + " successfully!");
                }
            }
        } catch (NoSuchWindowException e) {
            System.out.println("Window: " + windowTitle + " cound not found!" + e.fillInStackTrace());
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
        System.out.println(String.format("更改前cookie:%s -> %s -> %s -> %s -> %s",
                cooke.getDomain(), cooke.getName(), cooke.getValue(), cooke.getExpiry(), cooke.getPath()));
        driver.manage().deleteCookieNamed(cookieName);
        Cookie c = new Cookie(cookieName, cookieValue, cooke.getDomain(), cooke.getPath(), cooke.getExpiry());
        driver.manage().addCookie(c);
        Cookie cooke1 = driver.manage().getCookieNamed(cookieName);
        System.out.println(String.format("更改后cookie:%s -> %s -> %s -> %s -> %s", cooke1.getDomain(), cooke1.getName(), cooke1.getValue(), cooke1.getExpiry(), cooke1.getPath()));
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
