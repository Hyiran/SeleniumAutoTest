package com.framework.webdriver;

import com.framework.data.provider.CsvDataProvider;
import com.framework.data.provider.ExcelDataProvider;
import com.framework.data.provider.XmlDataProvider;
import org.testng.annotations.DataProvider;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Created by caijianmin on 2016/5/25.
 */
public class DriverBase {

    @DataProvider(name = "xml")
    protected Object[][] xmlData(Method m) {
        return new XmlDataProvider().getData(m.getName(), m.getDeclaringClass().getSimpleName() + ".xml");
    }

    @DataProvider(name = "csv")
    protected Object[][] csvData(Method m) {
        return new CsvDataProvider().getData(m.getName() + ".csv", m.getDeclaringClass().getSimpleName());
    }

    @DataProvider(name = "excel")
    public Iterator<Object[]> excelData(Method m) throws FileNotFoundException {
        return new ExcelDataProvider(m.getName(), m.getDeclaringClass().getSimpleName());
    }



}

