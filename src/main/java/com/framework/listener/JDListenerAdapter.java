package com.framework.listener;

import com.framework.data.util.ElementBase;
import com.framework.page.PageBase;
import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.util.Log;
import com.framework.webdriver.RunTest;
import org.apache.log4j.Logger;
import org.testng.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Caijianmin
 */
public class JDListenerAdapter extends TestListenerAdapter implements IExecutionListener, ISuiteListener, IInvokedMethodListener {
    public static String locator = null;
    public static String type = null;
    public static Config config = new Config(Constants.config);
    public static Logger log = Log.getInstance();

    //测试集开始监听
    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        log.info(String.format("测试集【%s】执行开始 ", testContext.getName()));
    }

    //测试集结束监听
    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        log.info(String.format("测试集【%s】执行结束", testContext.getName()));
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        boolean USESCREENSHOT = System.getProperty("UseScreenshot", "").equalsIgnoreCase("true");
        log.error("=======================================================");
        log.error("测试方法 【" + tr.getName() + "】 执行失败");
        String imgName;
        if (RunTest.getDriver() != null) {
            log.info("请参考：");
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
        log.info("=======================================================");
        log.info("测试方法 【" + tr.getName() + "】 执行跳过");
        log.info("=======================================================");
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        log.info("=======================================================");
        log.info("测试方法 【" + tr.getName() + "】 执行成功！");
        log.info("=======================================================");
    }


    public void onExecutionStart() {
        weclome();
        log.info("=======================================================");
        log.info("                    测试框架执行开始");
        log.info("=======================================================");
    }

    public void onExecutionFinish() {
        log.info("=======================================================");
        log.info("                    测试框架执行结束");
        log.info("=======================================================");
    }

    public void onStart(ISuite suite) {
        log.info("测试套件【" + suite.getName() + "】执行开始");
    }

    public void onFinish(ISuite suite) {
        log.info("测试套件【" + suite.getName() + "】执行结束");
    }

    private void weclome() {
        log.info(String.format("Starting Test Framework", new Object[0]));
    }

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        log.info("测试方法【" + method.getTestMethod().getMethodName() + "】执行开始");
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        log.info("测试方法【" + method.getTestMethod().getMethodName() + "】执行结束");
    }

}

