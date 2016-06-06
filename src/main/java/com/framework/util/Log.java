package com.framework.util;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.IOException;

public class Log {
    protected final Logger logger = Logger.getLogger(getClass());
    private static Log log;

    public Log() {
        File directory = new File(Constants.LogXML);
        try {
            //directory.getCanonicalPath();
            System.out.println("Load Config fileName" + directory.getCanonicalPath());
            DOMConfigurator.configure(directory.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getInstance() {
        if (log == null) {
            log = new Log();
        }
        return log.logger;
    }
}