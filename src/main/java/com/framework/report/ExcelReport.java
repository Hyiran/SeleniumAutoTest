package com.framework.report;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class ExcelReport {
    private String filepath;
    private static FormulaEvaluator evaluator = null;
    private static ExcelReport instance = null;
    private HSSFWorkbook book = null;//全局book
    private HSSFFont cellFontPass = null;//全局字体设置
    private HSSFFont cellFontFail = null;//全局字体设置
    private HSSFFont cellFont = null;//全局字体设置
    private HSSFFont cellFont1 = null;//全局字体设置

    public static ExcelReport getInstance() {
        if (instance == null) {
            instance = new ExcelReport();
        }
        return instance;
    }

    public ExcelReport(String filepath) throws Exception {
        this.filepath = filepath;
        InitSummarySheet(filepath);
    }

    public ExcelReport() {
        try {
            File path = new File("report");
            this.filepath = (path.getAbsolutePath() + "/Report.xls");
            FileInputStream in = new FileInputStream(new File(this.filepath));
            POIFSFileSystem poifile = new POIFSFileSystem(in);
            book = new HSSFWorkbook(poifile);
            cellFontPass = book.createFont();
            cellFontFail = book.createFont();
            cellFont = book.createFont();
            cellFont1 = book.createFont();
            InitSummarySheet(filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setEvalator(HSSFWorkbook book) {
        evaluator = book.getCreationHelper().createFormulaEvaluator();
    }

    public void InitSummarySheet(String filepath) throws Exception {
        String[] hosts = getLocalHost();
        /*HSSFWorkbook book = null;
        FileInputStream in = null;
        POIFSFileSystem poifile = null;
        in = new FileInputStream(filepath);
        poifile = new POIFSFileSystem(in);
        book = new HSSFWorkbook(poifile);*/
        HSSFSheet sheet = book.getSheetAt(0);
        writevalue(sheet, 1, 4, hosts[0], null);
        writevalue(sheet, 2, 4, hosts[1], null);
        writevalue(sheet, 0, 4, System.getProperty("WebDriver.Browser"), null);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        writevalue(sheet, 0, 8, df.format(new Date()), null);
        df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        writevalue(sheet, 1, 8, df.format(new Date()), null);
        FileOutputStream out = new FileOutputStream(filepath);
        book.write(out);
        out.close();
    }

    public static String getCellString(HSSFSheet sheet, int column, int row) {
        HSSFRow crow = sheet.getRow(row);
        if (null == crow) {
            return "";
        }
        HSSFCell cell = crow.getCell(column);
        if (null == cell) {
            return "";
        }
        return getStringCellValue(cell);
    }

    public static int getCellNum(HSSFSheet sheet, int column, int row) {
        HSSFRow crow = sheet.getRow(row);
        if (null == crow) {
            return 0;
        }
        HSSFCell cell = crow.getCell(column);
        if (null == cell) {
            return 0;
        }
        String cellvalue = "".equals(getStringCellValue(cell)) ? "0" : getStringCellValue(cell);
        return Integer.parseInt(cellvalue);
    }

    public static String getDateCell(HSSFSheet sheet, int column, int row) {
        HSSFRow crow = sheet.getRow(row);
        if (null == crow) {
            return "";
        }
        HSSFCell cell = crow.getCell(column);
        if (null == cell) {
            return "";
        }
        String cellvalue = "".equals(getDateCellValue(cell)) ? "" : getDateCellValue(cell);
        return cellvalue;
    }

    public static HSSFCell getCell(HSSFSheet sheet, int column, int row) {
        HSSFRow crow = sheet.getRow(row);
        if (null == crow) {
            return null;
        }
        HSSFCell cell = crow.getCell(column);
        return cell;
    }

    public static String evaluatorCell(HSSFCell cell) {
        CellValue cellvalue = evaluator.evaluate(cell);
        return cellvalue.toString();
    }

    public void writevalue(HSSFSheet sheet, int column, int row, String value, HSSFCellStyle style) {
        HSSFRow crow = sheet.getRow(row);
        if (null == crow) {
            crow = sheet.createRow(row);
        }
        HSSFCell cell = crow.getCell(column);
        if (null == cell) {
            cell = crow.createCell(column);
        }
        cell.setCellType(1);
        cell.setCellValue(value);
        if (null != style) {
            cell.setCellStyle(style);
        }
    }

    public void writevalue(HSSFSheet sheet, int column, int row, int value, HSSFCellStyle style) {
        HSSFRow crow = sheet.getRow(row);
        if (null == crow) {
            crow = sheet.createRow(row);
        }
        HSSFCell cell = crow.getCell(column);
        if (null == cell) {
            cell = crow.createCell(column);
        }
        cell.setCellValue(value);
        if (null != style) {
            cell.setCellStyle(style);
        }
    }

    public void Add2Report(String senario, String testcase, String testcaseDesc, String status, String detail, String remark, String picPath) {
        /*HSSFWorkbook book = null;
        FileInputStream in = null;
        POIFSFileSystem poifile = null;*/
        try {
            int summaryRow = 0;
            int resultRow = 0;
            int senarioRow = 0;
            /*in = new FileInputStream(new File(this.filepath));
            poifile = new POIFSFileSystem(in);
            book = new HSSFWorkbook(poifile);*/
            evaluator = book.getCreationHelper().createFormulaEvaluator();
            HSSFSheet summarysheet = book.getSheetAt(0);
            HSSFSheet senariosheet = book.getSheetAt(1);
            HSSFSheet testcasesheet = book.getSheetAt(2);
            HSSFSheet teststepsheet = book.getSheetAt(3);
            int A5 = getCellNum(summarysheet, 3, 4);
            int b15 = getCellNum(summarysheet, 1, 14);
            int c15 = getCellNum(summarysheet, 2, 14);
            summaryRow = b15 + 1 + A5;
            resultRow = c15 + 1 + b15;
            boolean newAction = false;
            boolean newSenario = false;
            for (int i = summaryRow; i >= 0; i--) {
                if (null == testcasesheet.getMergedRegion(i)) {
                    continue;
                }
                org.apache.poi.ss.util.CellRangeAddress ca = testcasesheet.getMergedRegion(i);
                int firstColumn = ca.getFirstColumn();
                int firstRow = ca.getFirstRow();
                String tempSenario = getCellString(testcasesheet, firstColumn, firstRow);
                if (tempSenario.equals(senario))
                    break;
                newSenario = true;
                break;
            }
            if (newSenario) {
                A5 = getCellNum(summarysheet, 3, 4) + 1;
                writevalue(summarysheet, 3, 4, A5, null);
                senarioRow = A5;
                HSSFCellStyle style = GetCustomBorderCellStyle(book, (short) 23, (short) 51, false);
                writevalue(testcasesheet, 0, summaryRow, senario, style);
                style = GetCustomBorderCellStyle(book, (short) 23, (short) 51, true);
                writevalue(senariosheet, 0, senarioRow, senario, style);
                HSSFRow crow = senariosheet.createRow(senarioRow);
                HSSFCell cell = crow.createCell(0);
                cell.setCellStyle(style);
                cell.setCellFormula("HYPERLINK(\"#" + testcasesheet.getSheetName() + "!A" + (summaryRow + 1) + "\", \"" + senario + "\")");
                style = GetCustomBorderCellStyle(book, (short) 23, (short) 51, false);
                writevalue(senariosheet, 1, senarioRow, 0, style);
                writevalue(senariosheet, 2, senarioRow, 0, style);
                writevalue(senariosheet, 5, senarioRow, summaryRow, style);
                style = GetCustomBorderCellStyle(book, (short) 23, (short) 17, false);
                writevalue(senariosheet, 3, senarioRow, 0, style);
                style = GetCustomBorderCellStyle(book, (short) 23, (short) 10, false);
                writevalue(senariosheet, 4, senarioRow, 0, style);
                testcasesheet.addMergedRegion(new org.apache.poi.hssf.util.CellRangeAddress(summaryRow, summaryRow, 0, 3));
                summaryRow += 1;
            }
            HSSFRow row = testcasesheet.getRow(summaryRow - 1);
            CellValue cellvalue = evaluator.evaluate(row.getCell(0));
            if (!testcase.equalsIgnoreCase(cellvalue.getStringValue())) {
                HSSFRow crow = testcasesheet.createRow(summaryRow);
                HSSFCell cell = crow.createCell(0);
                cell.setCellFormula("HYPERLINK(\"#" + teststepsheet.getSheetName() + "!A" + (resultRow + 1) + "\", \"" + testcase + "\")");
                HSSFCellStyle style = GetCustomBorderCellStyle(book, (short) 31, (short) 8, true);
                cell.setCellStyle(style);
                style = GetCustomBorderCellStyle(book, (short) 31, (short) 8, false);
                writevalue(testcasesheet, 3, summaryRow, testcaseDesc, style);
                A5 = getCellNum(summarysheet, 3, 4);
                int choice = status.equalsIgnoreCase("Pass") ? 0 : 1;
                if (status.equalsIgnoreCase("PASS"))
                    choice = 0;
                else if (status.equalsIgnoreCase("FAIL"))
                    choice = 1;
                else
                    choice = 2;
                int step;
                switch (choice) {
                    case 0:
                        int b13 = getCellNum(summarysheet, 1, 12) + 1;
                        int c13 = getCellNum(summarysheet, 2, 12) + 1;
                        writevalue(summarysheet, 1, 12, b13, null);
                        writevalue(summarysheet, 2, 12, c13, null);
                        step = getCellNum(senariosheet, 2, A5) + 1;
                        int npass = getCellNum(senariosheet, 3, A5) + 1;
                        writevalue(senariosheet, 2, A5, step, null);
                        writevalue(senariosheet, 3, A5, npass, null);
                        break;
                    case 1:
                        int b14 = getCellNum(summarysheet, 1, 13) + 1;
                        int c14 = getCellNum(summarysheet, 2, 13) + 1;
                        writevalue(summarysheet, 1, 13, b14, null);
                        writevalue(summarysheet, 2, 13, c14, null);
                        step = getCellNum(senariosheet, 2, A5) + 1;
                        int nfail = getCellNum(senariosheet, 4, A5) + 1;
                        writevalue(senariosheet, 2, A5, step, null);
                        writevalue(senariosheet, 4, A5, nfail, null);
                        break;
                    case 2:
                        writevalue(summarysheet, 1, 14, b15, null);
                        writevalue(summarysheet, 2, 14, c15, null);
                }
                int ncase = getCellNum(senariosheet, 1, A5) + 1;
                writevalue(senariosheet, 1, A5, ncase, null);
                newAction = true;

                if ("Fail".equalsIgnoreCase(status)) {
                    style = GetCustomBorderCellStyle(book, (short) 31, (short) 10, false);
                } else {
                    style = GetCustomBorderCellStyle(book, (short) 31, (short) 17, false);
                }
                writevalue(testcasesheet, 1, summaryRow, status, style);
                style = GetCustomBorderCellStyle(book, (short) 31, (short) 8, false);
                writevalue(testcasesheet, 2, summaryRow, 1, style);
            } else {
                int choice = status.equalsIgnoreCase("Pass") ? 0 : 1;
                A5 = getCellNum(summarysheet, 3, 4);
                int nstep;
                switch (choice) {
                    case 1:
                        int c14 = getCellNum(summarysheet, 2, 13) + 1;
                        writevalue(summarysheet, 2, 13, c14, null);
                        if ((c14 > 0) && ("Pass".equalsIgnoreCase(getCellString(testcasesheet, 1, summaryRow - 1)))) {
                            int b14 = getCellNum(summarysheet, 1, 13) + 1;
                            int b13 = getCellNum(summarysheet, 1, 12) - 1;
                            writevalue(summarysheet, 1, 13, b14, null);
                            writevalue(summarysheet, 1, 12, b13, null);
                            int sPassNum = getCellNum(senariosheet, 3, A5) - 1;
                            int sFailNum = getCellNum(senariosheet, 4, A5) + 1;
                            writevalue(senariosheet, 3, A5, sPassNum, null);
                            writevalue(senariosheet, 4, A5, sFailNum, null);
                        } else {
                            int sFailNum = getCellNum(senariosheet, 4, A5) + 1;
                            writevalue(senariosheet, 4, A5, sFailNum, null);
                        }
                        nstep = getCellNum(senariosheet, 2, A5) + 1;
                        writevalue(senariosheet, 2, A5, nstep, null);
                        HSSFCellStyle style = GetCustomBorderCellStyle(book, (short) 31, (short) 10, false);
                        writevalue(testcasesheet, 1, summaryRow - 1, status, style);
                        break;
                    case 0:
                        int c13 = getCellNum(summarysheet, 2, 12) + 1;
                        writevalue(summarysheet, 2, 12, c13, null);

                        nstep = getCellNum(senariosheet, 2, A5) + 1;
                        writevalue(senariosheet, 2, A5, nstep, null);
                }
                int cstep = getCellNum(testcasesheet, 2, summaryRow - 1) + 1;
                writevalue(testcasesheet, 2, summaryRow - 1, cstep, null);
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            writevalue(summarysheet, 2, 8, df.format(new Date()), null);
            if (newAction) {
                int step = getCellNum(testcasesheet, 2, summaryRow);
                HSSFCellStyle style = GetCustomBorderCellStyle(book, (short) 23, (short) 51, false);
                writevalue(teststepsheet, 0, resultRow, testcaseDesc, style);
                teststepsheet.addMergedRegion(new org.apache.poi.hssf.util.CellRangeAddress(resultRow, resultRow, 0, 3));
                resultRow += 1;
                style = GetCustomBorderCellStyle(book, (short) 31, (short) 8, false);
                writevalue(teststepsheet, 0, resultRow, "Step" + step, style);
            } else {
                int step = getCellNum(testcasesheet, 2, summaryRow - 1);
                HSSFCellStyle style = GetCustomBorderCellStyle(book, (short) 31, (short) 8, false);
                writevalue(teststepsheet, 0, resultRow, "Step" + step, style);
            }
            HSSFCellStyle style = null;

            if ("Fail".equalsIgnoreCase(status)) {
                style = GetCustomBorderCellStyle(book, (short) 31, (short) 10, false);
            } else {
                style = GetCustomBorderCellStyle(book, (short) 31, (short) 17, false);
            }
            writevalue(teststepsheet, 1, resultRow, status, style);
            style = GetCustomBorderCellStyle(book, (short) 31, (short) 8, false);
            writevalue(teststepsheet, 3, resultRow, remark, style);
            style = GetCustomBorderCellStyle(book, (short) 31, (short) 8, true);
            writevalue(teststepsheet, 2, resultRow, detail, style);
            HSSFRow crow = teststepsheet.getRow(resultRow);
            HSSFCell cell = crow.getCell(2);
            String[] aPicStr = picPath.split("\\\\");
            String tempStr = ".";
            for (int i = aPicStr.length - 1; i < aPicStr.length; i++) {
                tempStr = tempStr + "\\" + aPicStr[i];
            }
            HSSFHyperlink hyperlink = new HSSFHyperlink(1);
            hyperlink.setAddress(tempStr);
            cell.setHyperlink(hyperlink);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                FileOutputStream out = new FileOutputStream(this.filepath);
                book.write(out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private HSSFCellStyle GetCustomBorderCellStyle(HSSFWorkbook book, short colorIndex, short fontIndex, boolean underline) {
        HSSFCellStyle style = book.createCellStyle();
        style.setBorderBottom((short) 1);
        style.setBorderLeft((short) 1);
        style.setBorderTop((short) 1);
        style.setBorderRight((short) 1);
        style.setFillForegroundColor(colorIndex);
//        HSSFFont cellFont = book.createFont();
        if (fontIndex==10) {
            cellFontFail.setColor(fontIndex);
            cellFontFail.setFontName("微软雅黑");
            cellFontFail.setFontHeightInPoints((short) 10);
            cellFontFail.setBoldweight((short) 700);
            cellFontFail.setUnderline((byte) (!underline ? 0 : 1));
            style.setFont(cellFontFail);
        } else if (fontIndex==17){
            cellFontPass.setColor(fontIndex);
            cellFontPass.setFontName("微软雅黑");
            cellFontPass.setFontHeightInPoints((short) 10);
            cellFontPass.setBoldweight((short) 700);
            cellFontPass.setUnderline((byte) (!underline ? 0 : 1));
            style.setFont(cellFontPass);
        }else if (fontIndex==8){
            cellFont.setColor(fontIndex);
            cellFont.setFontName("微软雅黑");
            cellFont.setFontHeightInPoints((short) 10);
            cellFont.setBoldweight((short) 700);
//            cellFont.setUnderline((byte) (!underline ? 0 : 1));
            style.setFont(cellFont);
        }else if (fontIndex==51){
            cellFont1.setColor(fontIndex);
            cellFont1.setFontName("微软雅黑");
            cellFont1.setFontHeightInPoints((short) 10);
            cellFont1.setBoldweight((short) 700);
            cellFont1.setUnderline((byte) (!underline ? 0 : 1));
            style.setFont(cellFont1);
        }else {
            cellFont.setColor(fontIndex);
            cellFont.setFontName("微软雅黑");
            cellFont.setFontHeightInPoints((short) 10);
            cellFont.setBoldweight((short) 700);
            style.setFont(cellFont);
        }
        style.setFillPattern((short) 1);
        return style;
    }

    public String[] getLocalHost() throws Exception {
        String[] hosts = {"", ""};
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();

            System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if ((ip == null) || (!(ip instanceof Inet4Address)))
                    continue;
                hosts[0] = ip.getHostName();
                hosts[1] = ip.getHostAddress();
                return hosts;
            }
        }

        return hosts;
    }

    private static String getDateCellValue(HSSFCell cell) {
        String result = "";
        try {
            int cellType = cell.getCellType();
            if (cellType == 0) {
                Date date = cell.getDateCellValue();
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                result = df.format(date);
            } else if (cellType == 1) {
                String date = getStringCellValue(cell);
                result = date.replaceAll("[年月]", "-").replace("日", "").trim();
            } else if (cellType == 3) {
                result = "";
            } else if (cellType == 2) {
                Date date = cell.getDateCellValue();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                result = df.format(date);
            }
        } catch (Exception e) {
            System.out.println("日期格式不正确!");
            e.printStackTrace();
        }
        return result;
    }

    public static String getStringCellValue(HSSFCell cell) {
        String strCell = "";

        switch (cell.getCellType()) {
            case 1:
                strCell = cell.getStringCellValue();
                break;
            case 0:
                strCell = String.valueOf((int) cell.getNumericCellValue());
                break;
            case 4:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case 3:
                strCell = "";
                break;
            case 2:
                CellValue cellvalue = evaluator.evaluate(cell);
                strCell = String.valueOf((int) cellvalue.getNumberValue());
                cell.setCellFormula(cell.getCellFormula());
                break;
            default:
                strCell = "";
        }

        if ((strCell.equals("")) || (strCell == null)) {
            return "";
        }
        if (cell == null) {
            return "";
        }
        return strCell;
    }

    public boolean hasMerged(HSSFSheet sheet, Region region) {
        for (int row = region.getRowFrom(); row < region.getRowTo(); row++) {
            for (short col = region.getColumnFrom(); col < region.getColumnTo(); col = (short) (col + 1)) {
                for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                    Region r = sheet.getMergedRegionAt(i);
                    if (r.contains(row, col)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}