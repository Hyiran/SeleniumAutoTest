package com.framework.report;

/**
 * Created by caijianmin on 2016/1/6.
 */

import org.testng.IClass;

import java.util.Comparator;

class TestClassComparator implements Comparator<IClass> {
    public int compare(IClass class1, IClass class2) {
        return class1.getName().compareTo(class2.getName());
    }
}