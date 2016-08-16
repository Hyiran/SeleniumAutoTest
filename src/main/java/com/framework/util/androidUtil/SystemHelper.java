package com.framework.util.androidUtil;

import com.framework.page.Page;
import com.framework.page.PageBase;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SystemHelper {

    public static String getUserdir() {
        return System.getProperty("user.dir");
    }







    public static String getMacAddress() {
        String macAddress = "";
        try {
            boolean flag = false;
            NetworkInterface networkInterface = null;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface
                        .getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress address = inetAddresses.nextElement();
                    String ipAddr = address.getHostAddress();
                    if (ipAddr != null
                            && (ipAddr.startsWith("192.") || ipAddr
                            .startsWith("10."))) {
                        flag = true;
                        break;
                    }
                }
                if (flag)
                    break;
            }
            if (flag && networkInterface != null && networkInterface.isUp()) {
                byte[] addr = networkInterface.getHardwareAddress();
                if (addr != null && addr.length > 0) {
                    for (byte add : addr) {
                        String hex = Integer.toHexString(add & 0xff);
                        while (hex.length() < 2)
                            hex = "0" + hex;
                        macAddress += hex;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return macAddress.toUpperCase();
    }

    /**
     * 执行当前系统的命令，成功返回true,失败返回false;
     *
     * @param command
     * @return
     */

    public static void exec(String command) {
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static java.lang.Process exec(String[] command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return process;
    }

    /**
     * android清除缓存
     */
    public static void clearAndroidCache() {
        if (System.getProperty("os.name").contains("Mac")) {
            SystemHelper
                    .exec("/usr/local/bin/adb shell pm clear jd.cbt.android.market.russia");
        } else {
            SystemHelper.exec("adb shell pm clear jd.cbt.android.market.russia");
        }
    }

    /**
     * ios清除缓存
     *
     * @return
     */
    public static boolean clearIOSCache() {
        try {
            SystemHelper
                    .exec("/bin/rm -rf /Users/"
                            + System.getProperty("user.name")
                            + "/Library/Application Support/iPhone Simulator/7.1/Applications/"
                            + System.getProperty("IOS.RandomAppName")
                            + "/Library/");

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void launchAndroidApp() {
        if (System.getProperty("os.name").contains("Mac")) {
            SystemHelper
                    .exec("/usr/local/bin/adb shell am start jd.cbt.android.market.russia/.app.setting.activity.ActivityWelcome");
        } else {
            SystemHelper
                    .exec("adb shell am start jd.cbt.android.market.russia/.app.setting.activity.ActivityWelcome");
        }
    }

    /**
     * 日期转字符串方法
     *
     * @param date
     * @param format
     * @return
     */
    public static String transferDateToString(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    /**
     * 字符串转日期的方法
     *
     * @param dateString
     * @param dateFormat
     * @return
     * @throws ParseException
     */
    public static Date transferStringToDate(String dateString, String dateFormat)
            throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date date = simpleDateFormat.parse(dateString);
        return date;
    }

    /**
     * 获取adb命令在屏幕上的输出
     *
     * @param adbCommand 所要执行的adb命令
     * @param outputPath 命令输出内容所要保存到的文件路径
     */
    public static void getAdbExecuteOutput(String adbCommand, String outputPath) {
        try {
            // 执行 ADB 命令
            Process process = Runtime.getRuntime().exec(adbCommand);

            // 从输入流中读取文本
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            // 构造一个写出流并指定输出文件保存路径
            FileWriter fireWriter = new FileWriter(new File(outputPath));

            String line = null;

            // 循环读取
            while ((line = br.readLine()) != null) {
                // 循环写入
                fireWriter.write(line + "\n");
            }

            // 刷新输出流
            fireWriter.flush();

            // 关闭输出流
            fireWriter.close();

            // 关闭输出流
            process.getOutputStream().close();

            System.out.println("程序执行完毕!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取adb命令输出中的相关字段
     *
     * @param adbCommand 要执行的adb命令
     * @param findFilter 要查找的关键字段
     * @return
     */
    public static String getAdbExecuteOutputTargetContent(String adbCommand,
                                                          String findFilter) {

        String result = null;
        try {
            Process process = Runtime.getRuntime().exec(adbCommand);

            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            while ((result = bf.readLine()) != null) {
                if (result.contains(findFilter)) {
                    break;
                }
            }

            process.getOutputStream().close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }


    /**
     * 将long型时间转化为具体的小时分钟秒格式，并以字符串形式返回，如果传入的时间long小于1000则返回xx毫秒 其余情况都会返回
     * xx小时xx分钟xx秒
     */
    public static String transferLongToTimeStr(long timeduration) {
        long hourDivide = 3600 * 1000;
        long minDivide = 1000 * 60;
        long secDivide = 1000;

        long hour = 0;
        long min = 0;
        long sec = 0;
        if (timeduration < 0) {
            return "时间长不能为负，请检查数据源是否合法";
        } else if (timeduration < 1000) {
            return timeduration + "毫秒";
        } else {
            hour = timeduration / hourDivide;
            min = (timeduration % hourDivide) / minDivide;
            sec = ((timeduration % hourDivide) % minDivide) / secDivide;
            return hour + "小时" + min + "分钟" + sec + "秒";
        }
    }

    public static Date[] getDateFromStringBatched(String[] allTimeStrings,
                                                  String format) throws ParseException {
        Date[] dates = new Date[allTimeStrings.length];
        for (int i = 0; i < allTimeStrings.length; i++) {
            dates[i] = SystemHelper.transferStringToDate(allTimeStrings[i],
                    format);
        }
        return dates;
    }

    public static String[] filterAllStringByRegex(
            String[] stringArrayNeedsToTrans, String Regex) {
        Pattern p = Pattern.compile(Regex);
        StringBuffer sb = new StringBuffer();
        Matcher m = null;
        for (int i = 0; i < stringArrayNeedsToTrans.length; i++) {
            m = p.matcher(stringArrayNeedsToTrans[i]);
            if (m.find()) {
                sb.append(m.group());
                sb.append(";");
            }
        }
        String[] results = sb.toString().split(";");
        return results;
    }

    public static String filterSingleStringByRegex(String stringNeedToTrans,
                                                   String Regex) {
        Pattern p = Pattern.compile(Regex);
        Matcher m = p.matcher(stringNeedToTrans);
        if (m.find()) {
            return m.group();
        } else {
            return null;
        }
    }

    public static String[] getAllFileNames(String filePath) {
        File targetFile = new File(filePath);
        String[] allFilesName = null;

        if (!targetFile.exists()) {
            allFilesName = new String[1];
            allFilesName[0] = "该文件夹不存在";
        } else if (targetFile.list().length == 0) {
            allFilesName = new String[1];
            allFilesName[0] = "该文件夹为空";
        } else if (targetFile.list().length == 1) {
            File target1 = new File(filePath + "/" + targetFile.list()[0]);
            if (target1.isHidden()) {
                allFilesName = new String[1];
                allFilesName[0] = "该文件夹为空";
            } else if (targetFile.list()[0].matches("[a-zA-Z0-9]+[.]{1}[pPjJ]{1}[pPnN]{1}[gG]{1}")) {
                allFilesName = targetFile.list();
            }
        } else {
            allFilesName = targetFile.list();
        }
        return allFilesName;
    }

    public static Boolean serverState(String url){
        try {
            if (PageBase.getRequestStatus(url) != 200) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }



}
