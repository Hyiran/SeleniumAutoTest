package com.framework.report;

/**
 * Created by caijianmin on 2016/1/6.
 */

import org.testng.ITestResult;

import java.util.Comparator;

class TestResultComparator implements Comparator<ITestResult> {

    public int compare(ITestResult result1, ITestResult result2) {
        return result1.getName().compareTo(result2.getName());
    }
}