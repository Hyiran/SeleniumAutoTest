package com.framework.report;

/**
 * Created by caijianmin on 2016/1/6.
 */
public class ReportNGException extends RuntimeException
{
    public ReportNGException(String string)
    {
        super(string);
    }

    public ReportNGException(String string, Throwable throwable) {
        super(string, throwable);
    }
}