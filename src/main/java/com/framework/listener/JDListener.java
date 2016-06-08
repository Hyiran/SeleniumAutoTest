package com.framework.listener;

import com.framework.util.Log;
import org.apache.log4j.Logger;
import org.testng.*;


public class JDListener implements IExecutionListener, ISuiteListener, IInvokedMethodListener {

    public static Logger log = Log.getInstance();

    public void onExecutionStart() {
        weclome();
        System.out.println("=======================================================");
        System.out.println("                    测试框架执行开始");
        System.out.println("=======================================================");
    }

    public void onExecutionFinish() {
        System.out.println("=======================================================");
        System.out.println("                    测试框架执行结束");
        System.out.println("=======================================================");
    }

    public void onStart(ISuite suite) {
        System.out.println("测试套件【" + suite.getName() + "】执行开始");
    }

    public void onFinish(ISuite suite) {
        System.out.println("测试套件【" + suite.getName() + "】执行结束");
    }

    private void weclome() {
        System.out.println(String.format("Starting  Test Framework", new Object[0]));
    }

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        System.out.println("测试方法【" + method.getTestMethod().getMethodName() + "】执行开始");
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        System.out.println("测试方法【" + method.getTestMethod().getMethodName() + "】执行结束");
    }
}