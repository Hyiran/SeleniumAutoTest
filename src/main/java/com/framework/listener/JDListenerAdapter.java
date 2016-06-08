package com.framework.listener;

import com.framework.data.util.ElementBase;
import com.framework.page.PageBase;
import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.util.Log;
import com.framework.webdriver.RunTest;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JD TestFrameWork Listener Adapter
 *
 * @author Nihuaiqing
 */
public class JDListenerAdapter extends TestListenerAdapter  {
    public static String locator = null;
    public static String type = null;
    public static Config config = new Config(Constants.config);
    public static Logger log = Log.getInstance();

    //测试集开始监听
    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        System.out.println(String.format("测试集【%s】执行开始 ", testContext.getName()));
    }

    //测试集结束监听
    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        System.out.println(String.format("测试集【%s】执行结束", testContext.getName()));
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        boolean USESCREENSHOT = System.getProperty("UseScreenshot", "").equalsIgnoreCase("true");
        log.error("=======================================================");
        log.error("测试方法 【" + tr.getName() + "】 执行失败");
        String imgName;
        if (RunTest.getDriver() != null) {
            if (USESCREENSHOT) {
                //截图
                SimpleDateFormat df = new SimpleDateFormat("MMddHHmmss");
                imgName = tr.getName() + "_" + df.format(new Date());
                new ElementBase().lightElement(locator, type);
                PageBase.screenShot(imgName);
            }
        }
        String errorUrl = RunTest.getDriver().getCurrentUrl();
        log.error("getCurrentPageURL====================================" + errorUrl);


    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        System.out.println("=======================================================");
        System.out.println("测试方法 【" + tr.getName() + "】 执行跳过");
        System.out.println("=======================================================");
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        System.out.println("=======================================================");
        System.out.println("测试方法 【" + tr.getName() + "】 执行成功！");
        System.out.println("=======================================================");
    }

}

