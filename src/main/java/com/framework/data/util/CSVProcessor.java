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
    catch (FileNotFoundException e1)
    {
      e1.printStackTrace();
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    d.open(reader);
    HashMap<String, Object> es = new HashMap<String, Object>();
    while (d.hasNext()) {
      Element e = (Element)d.next();
      es.put(e.name, e);
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