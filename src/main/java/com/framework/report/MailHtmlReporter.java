package com.framework.report;

/**
 * Created by caijianmin on 2016/1/6.
 */


import com.framework.listener.ReportListener;
import com.framework.mail.JDMailInfo;
import com.framework.mail.SendMail;
import org.apache.velocity.VelocityContext;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by tianjing5 on 2015/12/22.
 */

public class MailHtmlReporter extends AbstractReporter {
    private static final String TEMPLATES_PATH = "com/test/util/qa/report/templates/html/";
    private static final String REPORT_DIRECTORY = "";
    private static final String MAIL_FILE = "mailTestNG.vm";
    private static final String MAIL_FILE_NAME = "mailTestNG.html";
    private static final String BUILD_URL = ReportListener.BUILD_URL;
    private static final String JOB_NAME = ReportListener.JOB_NAME;
    public static String imgPath = ""; //获取失败截图的本地路径
    public static String imgPathAbs = "";
    public static String imgUrl = "";//云端url
    public static String Subject;
    private static final ReportNGUtils UTILS = new ReportNGUtils();
    public static Map<String, List<String>> errorUrlData = new HashMap<String, List<String>>();
    public static Map<String, List<String>> errorDesData = new HashMap<String, List<String>>();
    public static Map<String, List<String>> errorImageData = new HashMap<String, List<String>>();
    public static String currenLog = "";//用于存储最后一条LOG信息
    private static boolean flag = true;
    private JDMailInfo jdMailInfo = new JDMailInfo();//邮件发送消息类初始化

    /*初始化配置文件*/
    static {

    }

    public MailHtmlReporter() {
        super(TEMPLATES_PATH);
    }

    public void SendMailHTML(String content, String Subject) {
        jdMailInfo.setContent(content);
        jdMailInfo.setSubject(Subject);
        jdMailInfo.addAttachment("./report/Report.xls");
        jdMailInfo.addAttachment("./report/logs/error.log");
        jdMailInfo.addAttachment("./report/logs/TestResult.log");
        SendMail sendMail = new SendMail(jdMailInfo);
        sendMail.sendStart();
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectoryName) {
        if (xmlSuites == null || xmlSuites.size() == 0) {
            return;
        }

        System.out.println("flagNew++++++++++++++++++++++++++++" + flag);
        removeEmptyDirectories(new File(outputDirectoryName));
        outputDirectoryName = addTimeStampToPath(outputDirectoryName);
        File outputDirectory = new File(outputDirectoryName, REPORT_DIRECTORY);
        outputDirectory.mkdirs();

        for (int i = 0; i < xmlSuites.size(); i++) {
            ISuite iSuite = suites.get(i);
            if (flag) {
                try {
                    createMailTestNG(iSuite, outputDirectory);
                    String htmlContent = getHTMLContent(outputDirectoryName + File.separator + MAIL_FILE_NAME);//读文件
                    Subject = System.getProperty("Mail.Subject", "");

                    Map<String, ISuiteResult> iSuiteResults = iSuite.getResults();
                    String key = iSuiteResults.keySet().iterator().next();//第一个的key
                    ISuiteResult result = iSuiteResults.get(key);
                    ITestContext testContext = result.getTestContext();
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat f = new SimpleDateFormat("[yyyy年MM月dd日]");
                    if (testContext.getFailedTests().size() > 0) {
                        Subject = "失败_" + JOB_NAME + Subject + f.format(c.getTime());
                        this.SendMailHTML(htmlContent, Subject);
                    } else if ("true".equalsIgnoreCase(System.getProperty("isSendMailonSuccess", "true"))) {
                        Subject = "成功_" + JOB_NAME + Subject + f.format(c.getTime());
                        this.SendMailHTML(htmlContent, Subject);
                    } else {
                        System.out.println("系统配置为运行成功后不发送邮件!");
                        Subject = "成功_" + JOB_NAME + Subject + f.format(c.getTime());
                        String excludeAddress = System.getProperty("excludeAddress");
                        if (null != excludeAddress && !"".equals(excludeAddress)) {
                            jdMailInfo.setToAddress(excludeAddress);
                            this.SendMailHTML(htmlContent, Subject);
                        }
                    }
                    /*xmlNameSet.add(xmlSuiteName);*/

                } catch (Exception e) {
                    throw new ReportNGException("Failed generating HTML report!", e);
                }
            }
        }
        flag = false;

    }


    private void createMailTestNG(ISuite iSuite, File outputDirectory) throws Exception {
        VelocityContext context = createContext();
        boolean USESCREENSHOT = System.getProperty("UseScreenshot", "").equalsIgnoreCase("true");

        Map<String, ISuiteResult> iSuiteResults = iSuite.getResults();
        //由于限定规则，xml中不能配置多个test跟标签,默认只拿第一个
        String key = iSuiteResults.keySet().iterator().next();//第一个的key
        ISuiteResult result = iSuiteResults.get(key);
        ITestContext testContext = result.getTestContext();

        Map<String, Object> htmlData = new HashMap<String, Object>();
        htmlData.put("projectName", testContext.getName());
        htmlData.put("startDate", getTime(testContext.getStartDate()));
        htmlData.put("duration", UTILS.formatDuration(UTILS.getDuration(testContext)));
        htmlData.put("buildUrl", BUILD_URL);
        htmlData.put("jobName", ReportListener.JOB_NAME);
        htmlData.put("isUseScreenshot", USESCREENSHOT);
        //统计走case维度，需要给方法名去重
        Map<String, Object> caseMap = new HashMap<String, Object>();
        caseMap.put("duration", UTILS.formatDuration(UTILS.getDuration(testContext)));
        caseMap.put("passed", removeRepeat(testContext.getPassedTests()).size());
        caseMap.put("skiped", removeRepeat(testContext.getSkippedTests()).size());
        caseMap.put("failed", removeRepeat(testContext.getFailedTests()).size());
        Integer noReptotal = removeRepeat(testContext.getPassedTests()).size() + removeRepeat(testContext.getSkippedTests()).size() + removeRepeat(testContext.getFailedTests()).size();
        double noRepRate = removeRepeat(testContext.getPassedTests()).size() * 100 / noReptotal;

        caseMap.put("noRepRate", noRepRate);
        htmlData.put("caseMap", caseMap);

        //统计走step维度，不需要给方法名去重
        Map<String, Object> stepMap = new HashMap<String, Object>();
        stepMap.put("duration", UTILS.formatDuration(UTILS.getDuration(testContext)));
        stepMap.put("passed", testContext.getPassedTests().size());
        stepMap.put("skiped", testContext.getSkippedTests().size());
        stepMap.put("failed", testContext.getFailedTests().size());
        Integer alltotal = testContext.getPassedTests().size() + testContext.getSkippedTests().size() + testContext.getFailedTests().size();
        double allRate = testContext.getPassedTests().size() * 100 / alltotal;
        stepMap.put("allRate", allRate);//通过率
        htmlData.put("stepMap", stepMap);

        //失败的方法列表
        List<Map<String, String>> failedDataList = new ArrayList<Map<String, String>>();
        //去重
        List<ITestNGMethod> errorMethodList = removeRepeat(testContext.getFailedTests());

        for (ITestNGMethod method : errorMethodList) {
            String failedName = method.getMethodName();

            List<String> errorDesList = errorDesData.get(failedName);
            List<String> errorUrlList = errorUrlData.get(failedName);
            List<String> errorImageList = errorImageData.get(failedName);

            File dir = new File("");// 参数为空
            try {
                String imgOutFile = dir.getCanonicalPath();
                // 失败方法如果被参数话需要根据方法名获取失败信息list
                for (int i = 0; i < errorDesList.size(); i++) {
                    String errorDesc = errorDesList.get(i);
                    String errorUrl = errorUrlList.get(i);
                    Map<String, String> data = new HashMap<String, String>();
                    data.put("errorDesc", errorDesc);
                    data.put("errorUrl", errorUrl);
                    data.put("methodName", failedName);
                    //上传图片
                    String imgPathData = errorImageList.get(i);
                    imgPathAbs = imgOutFile + File.separator + "test-output" + File.separator + "screenshot" + File.separator + imgPathData + ".jpg";
                    /*FileModel fileModel = new FileModel();
                    fileModel.setFileName(imgPathAbs);
                    fileModel.setDomainId("screenshot");
                    //拿到京东云存储返回的图片url
                    StorageService storageService = new StorageServiceImpl();
                    Result imgResUrl = storageService.uploadFile(fileModel);
                    imgUrl = imgResUrl.getUrl();
                    System.out.println("access url:" + imgResUrl.getUrl());*/
                    data.put("imgUrl", imgUrl);
                    failedDataList.add(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        htmlData.put("failedDataList", failedDataList);

        //成功的方法列表
        List<Map<String, Object>> successData = new ArrayList<Map<String, Object>>();
        //去重
        List<ITestNGMethod> successMethodList = removeRepeat(testContext.getPassedTests());
        for (ITestNGMethod method : successMethodList) {
            String passedName = method.getMethodName();
            String passedDesc = method.getDescription();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("methodName", passedName);
            map.put("methodDesc", passedDesc);
            successData.add(map);
        }

        htmlData.put("successData", successData);
        context.put("htmlData", htmlData);
//        System.out.println("====================htmlData是"+htmlData);
        generateFile(new File(outputDirectory, MAIL_FILE_NAME), MAIL_FILE, context);
    }

    private List<ITestNGMethod> removeRepeat(IResultMap resultMap) {
        Map<String, ITestNGMethod> datamap = new HashMap<String, ITestNGMethod>();
        List<ITestNGMethod> testNGMethodList = new ArrayList<ITestNGMethod>(resultMap.getAllMethods());
        for (ITestNGMethod method : testNGMethodList) {
            datamap.put(method.getMethodName(), method);
        }
        List<ITestNGMethod> list = new ArrayList<ITestNGMethod>();
        //把datamap里的value add到list里面，返回list
        Set keys = datamap.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            list.add(datamap.get(key));
        }
        return list;

    }


    private static String getTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private String addTimeStampToPath(String path) {
        DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
        String dt = DATE_FORMAT.format(new Date());
        path = path + File.separator + System.getProperty("TestTimeStamp", dt);

        return path;
    }

    /**
     * 向step List 里面添加step
     * @param step step描述
     */
   /* public static void mailAddStep(String step){
        if(step.length()>100){//如果step信息过长进行截取
            step = step.substring(0,100);
        }
        steps.add(step);
    }*/
}
