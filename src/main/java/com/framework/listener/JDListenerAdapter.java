package com.framework.listener;

import com.framework.util.Config;
import com.framework.util.Constants;
import com.framework.util.Log;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import java.util.*;

/**
 * JD TestFrameWork Listener Adapter
 *
 * @author Nihuaiqing
 */
public class JDListenerAdapter extends TestListenerAdapter {
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
        boolean USESCREENSHOT = System.getProperty("UseScreenshot","").equalsIgnoreCase("true");

    }

    //封装邮件数据:处理test方法中传入参数，参数值可变的情况
    private Map<String,List<String>> processData(String methodName,Map<String,List<String>> errorDataMap,String errorValue){
        Set<String> methodSet = errorDataMap.keySet();

        if (methodSet.contains(methodName)){
            //测试方法已存在,直接add错误信息
            List<String> errorValueList = errorDataMap.get(methodName);
            errorValueList.add(errorValue);
        }else{
            List<String> errorValueList = new ArrayList<String>();
            errorValueList.add(errorValue);
            errorDataMap.put(methodName,errorValueList);
        }
        return errorDataMap;
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
        log.info("=======================================================");
        log.info("测试方法 【"+tr.getName()+"】 执行跳过");
        log.info("=======================================================");
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        log.info("=======================================================");
        log.info("测试方法 【"+tr.getName()+"】 执行成功！");
        log.info("=======================================================");
    }

}

