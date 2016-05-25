package com.framework.util;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private InputStream in;
    private Properties properties;
    private Logger logger;

    public Config(String path) {
        this.logger = Log.getInstance();
        try {
            this.in = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            this.logger.error(e);
        }
        this.properties = new Properties();
        load();
    }

    private void load() {
        try {
            this.properties.load(this.in);
        } catch (Exception e) {
            this.logger.error("载入配置文件异常", e);
        }
    }

    public String get(String strName) {
        String values = "";
        if (this.properties.getProperty(strName) != null) {
            values = this.properties.getProperty(strName).trim();
        }
        return values;
    }
}