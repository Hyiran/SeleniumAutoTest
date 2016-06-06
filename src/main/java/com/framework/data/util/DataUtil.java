package com.framework.data.util;

import com.framework.util.Constants;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class DataUtil {
    static DataUtil instance = null;
    private String senario = "";
    private String currentTestCase = "";
    private HashMap<String, LinkedHashMap<String, String>> testcaseMap = null;
    String rowname;
    String columnname;
    String ddate;
    int i;
    int j;

    @DataProvider(name = "exceldata")
    public static Object[][] providerData() {
        Object[][] obj = {{Integer.valueOf(1), Integer.valueOf(2)}, {Integer.valueOf(3), Integer.valueOf(4)}};

        return obj;
    }

    @DataProvider(name = "exceldata2")
    public static Iterator<Object[]> provider() {
        Object[] obj = {Integer.valueOf(1), Integer.valueOf(42)};

        return null;
    }

    public HashMap<String, LinkedHashMap<String, String>> getTestcaseMap() {
        return this.testcaseMap;
    }

    public void setTestcaseMap(HashMap<String, LinkedHashMap<String, String>> testcaseMap) {
        this.testcaseMap = testcaseMap;
    }

    public String getCurrentTestCase() {
        return this.currentTestCase;
    }

    public void setCurrentTestCase(String currentTestCase) {
        this.currentTestCase = currentTestCase;
    }

    public String getSenario() {
        if (this.senario.equals("")) {
            this.senario = ((String) GetSeniorData().get(0));
        }
        return this.senario;
    }

    public void setSenario(String senario) {
        this.senario = senario;
    }

    public static DataUtil getInstance() {
        if (instance == null) {
            instance = new DataUtil();
        }
        return instance;
    }

    public String getTestCaseName(String senario, String classname) {
        String searched = "";
        HashMap testcase = (HashMap) getTestcaseMap().get(senario);
        if (null != testcase) {
            Set<String> keys = testcase.keySet();
            for (String key : keys) {
                if (classname.equalsIgnoreCase(key)) {
                    searched = key;
                    break;
                }
            }
        }
        return searched;
    }

    public String getTestCaseDesc(String senario, String casename) {
        String desc = "";
        HashMap testcase = (HashMap) getTestcaseMap().get(senario);
        if (null != testcase) {
            desc = (String) testcase.get(casename);
        }
        return desc;
    }

    public String getpath() {
        String path = null;
        File directory = new File("data");
        try {
            path = directory.getAbsolutePath();
        } catch (Exception e) {
        }
        return path;
    }

    public int getclounmcount(String columnname) {
        String Path = getpath();
        String filePath = Path + "\\TestData.xls";
        try {
            FileInputStream fs = new FileInputStream(filePath);
            POIFSFileSystem ps = new POIFSFileSystem(fs);
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);

            for (int i = 0; i < 1; i++) {
                int j = 0;
                HSSFRow row = sheet.getRow(i);
                if (row != null) {
                    sheet.getColumnBreaks();
                    for (; j < row.getPhysicalNumberOfCells(); j++) {
                        HSSFCell cell = row.getCell(j);
                        if ((cell != null) &&
                                (cell.toString().equals(columnname))) {
                            this.j = j;
                            break;
                        }

                        if (j == row.getPhysicalNumberOfCells() - 1) {
                            this.j = -1;
                        }
                    }
                }
            }
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.j;
    }

    public int getrowcount(String rowname) {
        String Path = getpath();
        String filePath = Path + "\\TestData.xls";
        try {
            FileInputStream fs = new FileInputStream(filePath);
            POIFSFileSystem ps = new POIFSFileSystem(fs);
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFCell cell = null;
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                if (row != null) {
                    int j = 0;
                    sheet.getColumnBreaks();
                    for (; j < 1; j++) {
                        cell = row.getCell(j);
                        if ((cell == null) ||
                                (!cell.toString().equals(rowname))) continue;
                        this.i = i;
                        break;
                    }
                }

            }

            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.i;
    }

    public String GetLocaldata(String columname) {
        String result = "";
        String Path = getpath();
        List filepath = new ArrayList();

        findFiles(Path, getCurrentTestCase() + ".xls", filepath);
        if (filepath.size() == 0)
            return result;
        try {
            FileInputStream fs = new FileInputStream((String) filepath.get(0));
            POIFSFileSystem ps = new POIFSFileSystem(fs);
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);
                if (row == null)
                    continue;
                sheet.getColumnBreaks();
                HSSFCell cell = row.getCell(0);
                String key = " ";
                if (cell == null)
                    key = "";
                else {
                    key = cell.toString();
                }
                if (!columname.equalsIgnoreCase(key))
                    continue;
                result = row.getCell(1) == null ? "" : row.getCell(1).toString();
                break;
            }

            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String Getdata(String rowname, String columname) {
        String data = null;

        this.i = getrowcount(rowname);
        this.j = getclounmcount(columname);
        if (this.j == -1) {
            return "";
        }

        String Path = getpath();
        String filePath = Path + "\\TestData.xls";
        try {
            FileInputStream fs = new FileInputStream(filePath);
            POIFSFileSystem ps = new POIFSFileSystem(fs);
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);

            HSSFRow row = sheet.getRow(this.i);
            if (row != null) {
                sheet.getColumnBreaks();
                HSSFCell cell = row.getCell(this.j);
                if (cell == null)
                    data = "";
                else
                    data = cell.toString();
            }
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void SetData(String rowname, String columnname, String ddate) {
        this.i = getrowcount(rowname);
        this.j = getclounmcount(columnname);

        String Path = getpath();
        String filePath = Path + "\\TestData.xls";
        try {
            FileInputStream fs = new FileInputStream(filePath);
            POIFSFileSystem ps = new POIFSFileSystem(fs);
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = sheet.getRow(this.i);
            if (row != null) {
                HSSFCell cell = row.getCell(this.j);
                if (cell == null) {
                    cell = row.createCell(this.j);
                }
                cell.setCellValue(ddate);
            }
            fs.close();
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.flush();
            wb.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> GetSeniorData() {
        List list = new ArrayList();

        String Path = getpath();
        String filePath = Path + "\\TestData.xls";
        try {
            FileInputStream fs = new FileInputStream(filePath);
            POIFSFileSystem ps = new POIFSFileSystem(fs);
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                if (row != null) {
                    sheet.getColumnBreaks();
                    if ((row.getCell(0).toString() != "") && (!"".equals(row.getCell(0).toString()))) {
                        for (int j = 0; j < 1; j++) {
                            HSSFCell cell = row.getCell(j);
                            if ("".equals(cell.toString()))
                                continue;
                            list.add(cell.toString());
                        }
                    }
                }
            }
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            new Throwable();
        }
        return list;
    }

    public String findFiles(String baseDirName, String targetFileName, List filepath) {
        File baseDir = new File(baseDirName);
        if ((!baseDir.exists()) || (!baseDir.isDirectory())) {
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        }
        String tempName = null;

        File[] files = baseDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File tempFile = files[i];
            if (tempFile.isDirectory()) {
                findFiles(tempFile.getAbsolutePath(), targetFileName, filepath);
            } else if (tempFile.isFile()) {
                tempName = tempFile.getName();
                if (tempName.equalsIgnoreCase(targetFileName)) {
                    filepath.add(tempFile.getAbsolutePath());
                }
            }
        }
        return tempName;
    }

    public LinkedHashMap<String, String> GetTestCase(String filename) {
        LinkedHashMap<String, String> testcasemap = new LinkedHashMap<String, String>();
        filename = filename + ".xls";
        List filepath = new ArrayList();
        findFiles(".\\data", filename, filepath);
        if (filepath.size() == 0) {
            findFiles(".\\data", "RunList.xls", filepath);
        }
        String CasefilePath = (String) filepath.get(0);
        try {
            FileInputStream fs = new FileInputStream(CasefilePath);
            POIFSFileSystem ps = new POIFSFileSystem(fs);
            HSSFWorkbook wb = new HSSFWorkbook(ps);
            HSSFSheet sheet = wb.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                if (row != null) {
                    sheet.getColumnBreaks();
                    if ((row.getCell(2) == null) || ("N".equals(row.getCell(2).toString())))
                        continue;
                    if ((row.getCell(1) == null) || ("".equals(row.getCell(1).toString())))
                        break;
                    String testcase = row.getCell(1).toString().trim();

                    String testDesc = row.getCell(0).toString().trim() + Constants.Comma + row.getCell(3).toString().trim();
                    if (testcasemap.containsKey(testcase)) {
                        testcase = testcase + Constants.DUP_MARK + i;
                    }
                    testcasemap.put(testcase, testDesc);
                }
            }
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testcasemap;
    }
}