package com.framework.data.provider;

import java.util.ArrayList;
import java.util.List;

public class DataProviderImpl
  implements IData
{
  protected String defaultPath = "resources/testdata/data/";

  protected int start = 1;

  protected int max = 2147483647;

  protected List<Object[]> data = new ArrayList();

  public Object[][] getData(String caseName, String dataFile)
  {
    generateData(caseName, dataFile);
    return generateArrayData();
  }

  public Object[][] getData(String caseName, String dataFile, int startRowNum)
  {
    this.start = startRowNum;
    return getData(caseName, dataFile);
  }

  public Object[][] getData(String caseName, String dataFile, int startRowNum, int Length)
  {
    this.max = (startRowNum + Length);
    return getData(caseName, dataFile, startRowNum);
  }

  public String getDefaultPath() {
    return this.defaultPath;
  }

  public void setDefaultPath(String defaultPath) {
    this.defaultPath = defaultPath;
  }

  protected void generateData(String caseName, String dataFile)
  {
  }

  protected Object[][] generateArrayData()
  {
    int i = 0;
    Object[][] o = new Object[this.data.size()][];
    for (Object[] oo : this.data) {
      o[(i++)] = oo;
    }
    this.data.clear();
    return o;
  }
}