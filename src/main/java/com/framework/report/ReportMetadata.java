package com.framework.report;

/**
 * Created by caijianmin on 2016/1/6.
 */

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class ReportMetadata
{
    static final String PROPERTY_KEY_PREFIX = "Report.";
    static final String TITLE_KEY = "Report.title";
    static final String DEFAULT_TITLE = "Test Results Report";
    static final String COVERAGE_KEY = "Report.coverage-report";
    static final String EXCEPTIONS_KEY = "Report.show-expected-exceptions";
    static final String OUTPUT_KEY = "Report.escape-output";
    static final String XML_DIALECT_KEY = "Report.xml-dialect";
    static final String STYLESHEET_KEY = "Report.stylesheet";
    static final String LOCALE_KEY = "Report.locale";
    static final String VELOCITY_LOG_KEY = "Report.velocity-log";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm z");
    private final Date reportTime = new Date();

    public String getReportDate() {
        return DATE_FORMAT.format(this.reportTime);
    }

    public String getReportTime() {
        return TIME_FORMAT.format(this.reportTime);
    }

    public String getReportTitle() {
        return System.getProperty("Report.title", "Test Results Report");
    }

    public String getCoverageLink() {
        return System.getProperty("Report.coverage-report");
    }

    public File getStylesheetPath() {
        String path = System.getProperty("Report.stylesheet");
        return path == null ? null : new File(path);
    }

    public boolean shouldShowExpectedExceptions() {
        return System.getProperty("Report.show-expected-exceptions", "false").equalsIgnoreCase("true");
    }

    public boolean shouldEscapeOutput() {
        return System.getProperty("Report.escape-output", "true").equalsIgnoreCase("true");
    }

    public boolean allowSkippedTestsInXML() {
        return !System.getProperty("Report.xml-dialect", "testng").equalsIgnoreCase("junit");
    }

    public boolean shouldGenerateVelocityLog() {
        return System.getProperty("Report.velocity-log", "false").equalsIgnoreCase("true");
    }

    public String getUser() throws UnknownHostException {
        String user = System.getProperty("user.name");
        String host = InetAddress.getLocalHost().getHostName();
        String ip = InetAddress.getLocalHost().getHostAddress();
        return ip + "(" + user + '@' + host + ")";
    }

    public String getJavaInfo() {
        return String.format("Java %s (%s)", System.getProperty("java.version"), System.getProperty("java.vendor"));
    }

    public String getPlatform()
    {
        return String.format("%s %s (%s)", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
    }

    public Locale getLocale()
    {
        if (System.getProperties().containsKey("Report.locale")) {
            String locale = System.getProperty("Report.locale");
            String[] components = locale.split("_", 3);
            switch (components.length) {
                case 1:
                    return new Locale(locale);
                case 2:
                    return new Locale(components[0], components[1]);
                case 3:
                    return new Locale(components[0], components[1], components[2]);
            }
            System.err.println("Invalid locale specified: " + locale);
        }

        return Locale.getDefault();
    }
}