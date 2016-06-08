package com.framework.listener;

import com.framework.util.Log;
import org.apache.log4j.Logger;
import org.testng.*;


public class JDListener implements IExecutionListener, ISuiteListener, IInvokedMethodListener {

    public static Logger log = Log.getInstance();

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
        log.info(String.format("Starting JD Test Framework", new Object[0]));
    }

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        log.info("测试方法【" + method.getTestMethod().getMethodName() + "】执行开始");
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        log.info("测试方法【" + method.getTestMethod().getMethodName() + "】执行结束");
    }
}