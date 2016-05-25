package com.framework.data.util;

import com.framework.webdriver.ElementBase;
import org.jsefa.csv.annotation.CsvDataType;
import org.jsefa.csv.annotation.CsvField;

import java.util.Hashtable;

@CsvDataType
public class Element extends ElementBase implements Cloneable
{

  @CsvField(pos=1)
  public String name;

  @CsvField(pos=2)
  public String testData;

  @CsvField(pos=3)
  public String type;

  @CsvField(pos=4)
  public String locator;

  public Hashtable<String, String> getDatas()
  {
    Hashtable<String, String> datas = new Hashtable<String, String>();
    String[] ss = this.testData.split("<data>");
    for (int i = 0; i < ss.length; i++) {
      String s = ss[i].trim();
      if ((s != null) && (!s.equals(""))) {
        String[] s1 = s.split("<name>");
        datas.put(s1[0].trim(), s1[1].trim());
      }
    }
    return datas;
  }

  public String get(String key)
  {
    return (String)getDatas().get(key);
  }

  public Object clone()
  {
    Element el = null;
    try {
      el = (Element)super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return el;
  }
}