package com.framework.data.provider;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvDataProvider extends DataProviderImpl {

  public final char SEPARATE = '|';
  public final String ANNOTATION = "#";

  protected void generateData(String caseName, String dataFile)
  {
    List csvList = getCSVList(caseName, dataFile);
    if ((csvList.equals(null)) || (csvList.size() == 0)) {
      return;
    }
    for (int i = 1; i <= csvList.size(); i++)
      if ((i >= this.start) && (i <= this.max)) {
        String[] line = (String[])csvList.get(i - 1);
        if ((!line[0].replaceAll("\\s*", "").equals("")) && (!line[0].startsWith("#")))
          this.data.add(line);
      }
  }

  private List<String[]> getCSVList(String caseName, String dataFile)
  {
    List csvList = new ArrayList();
    try {
      CSVReader reader = new CSVReader(new FileReader(this.defaultPath + dataFile + File.separator + caseName), '|');
      String[] line = null;
      while ((line = reader.readNext()) != null)
        csvList.add(line);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return csvList;
  }
}