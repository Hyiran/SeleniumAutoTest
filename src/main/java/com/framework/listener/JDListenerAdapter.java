package com.framework.listener;

import com.jd.pop.qa.webdriver.DriverBase;
import com.jd.pop.qa.webdriver.ElementBase;
import com.test.util.qa.listener.EATReportListenerAdapter;
import com.test.util.qa.listener.MailHtmlReporterListener;
import com.test.util.qa.report.MailHtmlReporter;
import com.test.util.qa.util.InitProperties;
import com.test.util.qa.util.Logger;
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
public class JDListenerAdapter extends TestListenerAdapter {
    public static String locator = null;
    public static String type = null;


    //测试集开始监听
    @Override
    public void onStart(ITestContext testContext) {
        super.onStart(testContext);
        Logger.Defaultlog(String.format("测试集【%s】执行开始 ", testContext.getName()));
    }

    //测试集结束监听
    @Override
    public void onFinish(ITestContext testContext) {
        super.onFinish(testContext);
        Logger.Defaultlog(String.format("测试集【%s】执行结束", testContext.getName()));
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        boolean USESCREENSHOT = System.getProperty("UseScreenshot","").equalsIgnoreCase("true");
        super.onTestFailure(tr);
        String methodName= tr.getMethod().getMethodName();//失败的方法名
        Logger.Defaultlog("=======================================================");
        Logger.Defaultlog("测试方法 【"+tr.getName()+"】 执行失败");
        String imgName = "";
        if(DriverBase.driver!=null){
            Logger.Defaultlog("请参考：");
            if(USESCREENSHOT){
                //截图
                SimpleDateFormat df = new SimpleDateFormat("MMddHHmmss");
                imgName = tr.getName()+"_"+df.format(new Date());
                new ElementBase().lightElement(locator,type);
//                DriverBase.driver.takeScreenshot(imgName);
                DriverBase.driver.screenShot(imgName);
				/*MailHtmlReporter.errorImageList.add(imgName);*//*新增获取执行机上图片名*/

                //当在matrix上执行时
                if(InitProperties.MATRIXFLAG){
                    Logger.Defaultlog("<a href=\"./screenshot/"+imgName+".jpg"+"\" style=\"color:red;\">"+imgName+".jpg"+"</font></a>");
                }else if(EATReportListenerAdapter.ISONJENKINS){
                    EATReportListenerAdapter.methodInfo.put("ImgName",imgName+".jpg" );
                    Logger.Defaultlog(imgName+".jpg");
                }else{
                    Logger.Defaultlog("<a href=\"../screenshot/"+imgName+".jpg"+"\" style=\"color:red;\">"+imgName+".jpg"+"</font></a>");
                }
            }
        }
        String errorUrl =  DriverBase.getCurrentPageURL();
        Logger.Defaultlog("getCurrentPageURL===================================="+errorUrl);
        //获取当前失败方法在控制台的输出--失败步骤打印的log作为失败描述
        String failedDes = null;
        if (null!=MailHtmlReporter.currenLog&&!"".equals(MailHtmlReporter.currenLog)) {
            failedDes = methodName + MailHtmlReporter.currenLog;
        }else {
            failedDes = "报错方法:"+tr.getMethod().getMethodName();
        }

        //MailHtmlReporter.errorDesData.put(tr.getMethod().getMethodName(),failedDes);/*@tianjing获取当前出错方法的log信息2016.2.17 */
        //MailHtmlReporter.errorUrlData.put(tr.getMethod().getMethodName(),errorUrl);/*@tianjing获取当前出错页面URL2016.1.28 */
        MailHtmlReporter.errorDesData = processData(tr.getMethod().getMethodName(),MailHtmlReporter.errorDesData,failedDes);
        MailHtmlReporter.errorUrlData = processData(tr.getMethod().getMethodName(),MailHtmlReporter.errorUrlData,errorUrl);
        MailHtmlReporter.errorImageData = processData(tr.getMethod().getMethodName(),MailHtmlReporter.errorImageData,imgName);
        /**===============================================================================================**/
        MailHtmlReporterListener.errorDesData = processData(tr.getMethod().getMethodName(),MailHtmlReporterListener.errorDesData,failedDes);
        MailHtmlReporterListener.errorUrlData = processData(tr.getMethod().getMethodName(),MailHtmlReporterListener.errorUrlData,errorUrl);
        MailHtmlReporterListener.errorImageData = processData(tr.getMethod().getMethodName(),MailHtmlReporterListener.errorImageData,imgName);

        Logger.error(methodName + "失败原因为：" + failedDes + tr.getThrowable().getMessage());
        Logger.error("=======================================================");
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
        Logger.Defaultlog("=======================================================");
        Logger.Defaultlog("测试方法 【"+tr.getName()+"】 执行跳过");
        Logger.Defaultlog("=======================================================");
    }

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
        Logger.Defaultlog("=======================================================");
        Logger.Defaultlog("测试方法 【"+tr.getName()+"】 执行成功！");
        Logger.Defaultlog("=======================================================");
    }

}

