package com.framework.listener;

import com.framework.page.Page;
import com.framework.page.PageBase;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

/**
 * Created by caijianmin on 2016/6/10.
 */
public class MyEventListener implements WebDriverEventListener {
    @Override
    public void beforeNavigateTo(String s, WebDriver webDriver) {

    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {
        long contentHeight = (Long) ((JavascriptExecutor) driver).executeScript("return document.body.clientHeight ; ", new Object[0]);
        long availHeight = (Long) ((JavascriptExecutor) driver).executeScript("return window.screen.availHeight; ", new Object[0]);
        int count = (int)(contentHeight / availHeight);
        while (count-- > 0) {
            ((JavascriptExecutor)driver).executeScript("scroll(0," + (contentHeight - availHeight * count) + ");", new Object[0]);
            PageBase.delay(1);
        }
        ((JavascriptExecutor)driver).executeScript("scroll(0,0)", new Object[0]);
    }

    @Override
    public void beforeNavigateBack(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateBack(WebDriver webDriver) {

    }

    @Override
    public void beforeNavigateForward(WebDriver webDriver) {

    }

    @Override
    public void afterNavigateForward(WebDriver webDriver) {

    }

    @Override
    public void beforeFindBy(By by, WebElement webElement, WebDriver webDriver) {
       // System.out.println("开始查找元素：" + by.toString());
    }

    @Override
    public void afterFindBy(By by, WebElement webElement, WebDriver webDriver) {
       // System.out.println("查找元素后：" + by.toString());
    }

    @Override
    public void beforeClickOn(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterClickOn(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void beforeChangeValueOf(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void afterChangeValueOf(WebElement webElement, WebDriver webDriver) {

    }

    @Override
    public void beforeScript(String s, WebDriver webDriver) {

    }

    @Override
    public void afterScript(String s, WebDriver webDriver) {

    }

    @Override
    public void onException(Throwable throwable, WebDriver webDriver) {
        System.out.println("异常：" + throwable.getMessage());
    }
}
