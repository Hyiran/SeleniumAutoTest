package com.framework.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class InitProperties
{
  public static final boolean MATRIXFLAG = System.getProperty("user.dir").contains("C:\\Matrix\\STAF\\work");
  public static final String PFILEPATH = Constants.config;
  public static final String TIMEKEY = "TestTimeStamp";
  public static final String MILITIMEKEY = "TestMiliTimeStamp";
  private InputStreamReader fn = null;

  private Properties config = new Properties();

  public static Map<String, String> mapproperties = new HashMap<String, String>();

  public InitProperties()
  {
    init();
  }

  private void init()
  {
    try {
      this.fn = new InputStreamReader(new FileInputStream(System.getProperty("user.dir") + "/resources/config/CONFIG.properties"), "UTF-8");
      this.config.load(this.fn);
      if (!this.config.isEmpty()) {
        Set<Object> keys = this.config.keySet();
        for (Iterator<Object> i = keys.iterator(); i.hasNext(); ) { 
        	Object key = i.next();
            mapproperties.put(key.toString(), this.config.getProperty(key.toString()));
            if ((!System.getProperties().containsKey(key.toString())) && (!this.config.getProperty(key.toString()).isEmpty())) {
            	System.setProperty(key.toString(), this.config.getProperty(key.toString()));
            }
        }
        addTimeStampProperty();
        keys.clear();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        this.fn.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void addTimeStampProperty()
  {
    Date d = new Date();
    String dt = new SimpleDateFormat("yyyyMMddHHmmss").format(d);
    mapproperties.put("TestTimeStamp", dt);
    System.setProperty("TestTimeStamp", dt);
    String mdt = String.valueOf(d.getTime());
    mapproperties.put("TestMiliTimeStamp", mdt);
    System.setProperty("TestMiliTimeStamp", mdt);
  }

  public static void showAllSystemProperties()
  {
    Set<String> syskeys = mapproperties.keySet();
    for (Object key : syskeys) {
      if (System.getProperties().containsKey(key)) {
        System.out.println(key.toString() + "  " + System.getProperty(key.toString()));
      }
    }
    syskeys.clear();
  }
}