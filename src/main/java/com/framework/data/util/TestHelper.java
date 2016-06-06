package com.framework.data.util;

import com.framework.page.Page;

import java.io.File;
import java.util.HashMap;

public class TestHelper
{
  public static CSVProcessor cproc = new CSVProcessor();
  public static final String pageFilePath = "resources/testdata/pages";
  public static final String csv = ".csv";
  public static String pagePackage = "com.jd.web.page";
  public static final String testDataEncoding = "UTF-8";

  public static HashMap<String, Object> getTestData(String inFileName)
  {
    return cproc.deserialize(inFileName, "UTF-8");
  }

  public Page getPage(String pageName) throws Exception {
    pagePackage = System.getProperty("Project.pagePackage", pagePackage);
    Page page = null;
    try {
      page = (Page)Class.forName(pagePackage + "." + pageName).newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    if (page != null) {
      page.init(getTestData("resources/testdata/pages" + File.separator + pageName + ".csv"));
    }else {
      throw new Exception("初始化page失败");
    }
    return page;
  }
}