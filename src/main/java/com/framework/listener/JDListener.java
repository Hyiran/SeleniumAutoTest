package com.framework.listener;

import com.test.util.qa.util.Logger;
import org.testng.*;


public class JDListener implements IExecutionListener, ISuiteListener, IInvokedMethodListener {

    public void onExecutionStart() {
        weclome();

        Logger.Defaultlog("=======================================================");
        Logger.Defaultlog("                    测试框架执行开始");
        Logger.Defaultlog("=======================================================");
    }

    public void onExecutionFinish() {
        Logger.Defaultlog("=======================================================");
        Logger.Defaultlog("                    测试框架执行结束");
        Logger.Defaultlog("=======================================================");
    }

    public void onStart(ISuite suite) {
        Logger.Defaultlog("测试套件【" + suite.getName() + "】执行开始");
    }

    public void onFinish(ISuite suite) {
        Logger.Defaultlog("测试套件【" + suite.getName() + "】执行结束");
    }

    private void weclome() {
        Logger.Defaultlog(String.format("Starting JD Test Framework", new Object[0]));
    }

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        Logger.Defaultlog("测试方法【" + method.getTestMethod().getMethodName() + "】执行开始");
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
    }
}