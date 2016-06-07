package com.framework.data.util;

import org.jsefa.Deserializer;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;

import java.io.*;
import java.util.HashMap;

public class CSVProcessor
{
  public HashMap<String, Object> deserialize(String inFileName, String encoding)
  {
    CsvConfiguration config = new CsvConfiguration();
    config.setFieldDelimiter('|');

    Deserializer d = CsvIOFactory.createFactory(config, new Class[] { Element.class })
      .createDeserializer();

    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(new FileInputStream(inFileName), encoding);
    }
    catch (FileNotFoundException | UnsupportedEncodingException e1)
    {
      e1.printStackTrace();
    }
    HashMap<String, Object> es = null;
    try {
      d.open(reader);
      es = new HashMap<String, Object>();
      while (d.hasNext()) {
        Element e = d.next();
        es.put(e.name, e);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (reader != null) {
        reader.close();
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    d.close(true);
    return es;
  }
}