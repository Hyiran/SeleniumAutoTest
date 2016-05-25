package com.framework.data.provider;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by caijianmin on 2016/3/16.
 */
public class ExcelDataProvider implements Iterator<Object[]> {
    HSSFSheet hssfSheet;
    POIFSFileSystem poifsFileSystem;
    int colflag = 0;
    int rowflag = 0;
    int usedrowflag = 1;
    String[] colname = null;
    FileInputStream intputStream;
    public List<String> value;

    public ExcelDataProvider(String className, String filename) throws FileNotFoundException {
        this.intputStream = new FileInputStream(getPath(filename));
        value = new ArrayList<String>();
        this.poifsFileSystem = null;
        try {
            this.poifsFileSystem = new POIFSFileSystem(this.intputStream);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(this.poifsFileSystem);
            this.hssfSheet = hssfWorkbook.getSheet(className);
            HSSFRow hssfRow = this.hssfSheet.getRow(0);
            this.colflag = hssfRow.getPhysicalNumberOfCells();//获取excel实际有多少列数据
            int rows = this.hssfSheet.getLastRowNum();//获取一共有多少行
            this.colname = new String[this.colflag];
            for (int i = 0; i < this.colflag; i++) {
                this.colname[i] = this.hssfSheet.getRow(0).getCell(i).toString();//设置表头名称
            }
            for (int r = 1; r <= rows; r++) {
                try {
                    String v = this.hssfSheet.getRow(r).getCell(0).toString();
                    if ((v == null) || (v.equals(""))) {
                        break;
                    } else {
                        value.add(v);
                    }

                } catch (NullPointerException e) {
                    break;
                }
                this.rowflag += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasNext() {
        if ((this.rowflag > 0)
                && (!this.hssfSheet.getRow(this.rowflag).getCell(0).toString().equals(""))
                && (this.usedrowflag <= this.rowflag)) {
            return true;
        }
        try {
            this.intputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /*框架原来的next方法*/
    public Object[] next() {
        Object[] object = new Object[1];
        Map<String,String> map = new LinkedHashMap<String,String>();
        for (int c = 0; c < this.colflag; c++) {
            //ArrayList<String> list = new ArrayList<String>();
            String cellvalue="";
            try {
                cellvalue = this.hssfSheet.getRow(this.usedrowflag).getCell(c).toString();
                //list.add(cellvalue);
            } catch (Exception e) {
                //list.add("");
                map.put(this.colname[c], cellvalue);
                continue;
            }

            map.put(this.colname[c], cellvalue);
        }
        this.usedrowflag += 1;
        object[0] = map;
        return object;
    }

    /*框架修改后的next方法*/
    public Object[] nextnew() {
        Object[] object = new Object[colflag];
        for (int c = 0; c < this.colflag; c++) {
            try {
                String cellvalue = this.hssfSheet.getRow(this.usedrowflag).getCell(c).toString();
                object[c] = cellvalue;
            } catch (Exception e) {
                object[c] = "";
                continue;
            }
        }
        this.usedrowflag += 1;
        return object;
    }

    public void remove() {
    }

    public String getPath() {
        File file = new File("resources\\testdata\\data");
        String path = file.getAbsolutePath();
        path = path + "\\" + "TestData.xls";
        return path;
    }

    public String getPath(String filename) {
        File file = new File("resources\\testdata\\data");
        String path = file.getAbsolutePath();
        path = path + "\\" + filename + ".xls";
        return path;
    }
}