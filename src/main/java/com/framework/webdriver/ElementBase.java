package com.framework.webdriver;

import com.framework.data.util.Element;
import com.framework.listener.JDListenerAdapter;
import com.google.common.base.Function;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ElementBase
{
  private Element element = null;
  private WebDriver driver = RunTest.getDriver();
  private Long implicitlyWait=Long.parseLong(System.getProperty("implicitlyWait"));
  private Long webDriverWait=Long.parseLong(System.getProperty("webDriverWait"));

  public static void pressKey(int key) {
    try {
      Robot rob = new Robot();
      rob.keyPress(key);
      rob.keyRelease(key);
    } catch (AWTException localAWTException) {
    }
  }

  public static void delay(int time) {
    try {
      Thread.sleep(time * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void click()
  {
    scrollIntoView();
    getWebElement().click();
  }

  public void clear()
  {
    scrollIntoView();
    getWebElement().clear();
  }

  public void pressKey(CharSequence[] keysToSend)
  {
    getWebElement().sendKeys(keysToSend);
  }

  public void input(String text)
  {
    inputWithoutCheck(text);
    valueToBe(text);
  }

  public void inputByJs(String text)
  {
    jsExecutor(String.format("arguments[0].value=\"%s\"", new Object[] { text }));
    valueToBe(text);
  }

  public void inputWithoutCheck(String text)
  {
    scrollIntoView();
    WebElement e = getWebElement();
    e.clear();
    e.sendKeys(new CharSequence[] { text });
  }

  public void selectWithoutCheck(String text)
  {
    scrollIntoView();
    WebElement e = getWebElement();
    Select sel = new Select(e);
    sel.selectByVisibleText(text);
  }

  public void moveToElement()
  {
//    scrollIntoView();
    Actions action = new Actions(this.driver);
    WebElement e = getWebElement();
    action.moveToElement(e).build().perform();
  }

  public void actionClick()
  {
    scrollIntoView();
    Actions action = new Actions(this.driver);
    WebElement e = getWebElement();
    action.click(e).perform();
  }

  public void clickByJs(){
    jsExecutor("arguments[0].click();");
  }

  public void doubleClick()
  {
    scrollIntoView();
    Actions action = new Actions(this.driver);
    WebElement e = getWebElement();
    action.doubleClick(e).perform();
  }

  public void rightClick()
  {
    scrollIntoView();
    Actions action = new Actions(this.driver);
    WebElement e = getWebElement();
    action.contextClick(e).perform();
  }

  public void dragAndDropTo(Element element)
  {
    WebElement e = getWebElement();
    WebElement e2 = this.driver.findElement(getBy(element.locator, element.type));
    new Actions(this.driver).dragAndDrop(e, e2).perform();
  }

  public String getTextAlign()
  {
    return getWebElement().getCssValue("text-align");
  }

  public boolean isElementPresent(int timeout)
  {
    try
    {
      this.driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
      getWebElement();
      this.driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
      return true;
    } catch (NoSuchElementException e) {
      this.driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.SECONDS);
    }return false;
  }

  public void waitForElementPresent()
  {
    int time = 5;
    for (int i = 1; i < Math.ceil(webDriverWait / 10); i++) {
      delay(time);
      pressKey(27);
      if (isElementPresent(3)) {
        return;
      }
      pressKey(116);
      time *= 2;
    }
  }

  public void toBePresent()
  {
    new WebDriverWait(this.driver,webDriverWait).until(ExpectedConditions.presenceOfElementLocated(getBy()));
  }


public void toBeNotPresent()
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>(){
      public Boolean apply(WebDriver driver) {
        return Boolean.valueOf(!ElementBase.this.isElementPresent(2));
      }
    });
  }

  public boolean isVisible()
  {
    return getWebElement().isDisplayed();
  }

  public void toBeVisible()
  {
    new WebDriverWait(this.driver, webDriverWait).until(ExpectedConditions.visibilityOfElementLocated(getBy()));
  }

  public void toBeInvisible()
  {
    new WebDriverWait(this.driver, webDriverWait).until(ExpectedConditions.invisibilityOfElementLocated(getBy()));
  }

  public boolean isEnabled()
  {
    return getWebElement().isEnabled();
  }

  public void toBeEnable()
  {
    new WebDriverWait(this.driver, webDriverWait).until(ExpectedConditions.elementToBeClickable(getBy()));
  }

  public void toBeDisable()
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>() {
      public Boolean apply(WebDriver driver) {
        return !ElementBase.this.getWebElement().isEnabled();
      }
    });
  }

  public void textToBe(final String text)
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          return ElementBase.this.getText().equals(text);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
    });
  }

  public void textToBeNotEmpty()
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          if (ElementBase.this.getText().length() != 0) return true;
        } catch (Exception e) {
          e.printStackTrace();
        }
        return false;
      }
    });
  }

  public void textContains(final String text)
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          return ElementBase.this.getText().contains(text);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }
    });
  }

  public void valueToBe(final String value)
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>() {
      public Boolean apply(WebDriver driver) {
        return ElementBase.this.getAttribute("value").equals(value);
      }
    });
  }

  public void valueToBeNotEmpty()
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>() {
      public Boolean apply(WebDriver driver) {
        if (ElementBase.this.getAttribute("value").length() != 0) return true; return false;
      }
    });
  }

  public void valueContains( final String value)
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>() {
      public Boolean apply(WebDriver driver) {
        return ElementBase.this.getAttribute("value").contains(value);
      }
    });
  }

  public boolean isSelected()
  {
    return getWebElement().isSelected();
  }

  public void toBeSelected()
  {
    new WebDriverWait(this.driver, webDriverWait).until(ExpectedConditions.elementToBeSelected(getBy()));
  }

  public void toBeNotSelected()
  {
    new WebDriverWait(this.driver, webDriverWait).until(ExpectedConditions.elementSelectionStateToBe(getBy(), false));
  }

  public void toBeActive()
  {
    new WebDriverWait(this.driver, webDriverWait).until(new Function<WebDriver,Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.switchTo().activeElement().getLocation().equals(ElementBase.this.getWebElement().getLocation());
      }
    });
  }

  public void scrollIntoView()
  {
//    jsExecutor("arguments[0].scrollIntoView()");
  }

  public void jsExecutor(String script)
  {
    try
    {
      WebElement e = getWebElement();
      ((JavascriptExecutor)driver).executeScript(script, new Object[]{e});
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getAttribute(String attribute)
  {
    return getWebElement().getAttribute(attribute);
  }

  public String getText()
  {
    String text=this.getWebElement().getText();
    /*if (text!=null&&!"".equals(text)) {
      int len=4;
      if (len>text.length()){
        len=text.length();
      }
      if (isMatcheMessy(text.substring(0,len))) {
        Logger.error("文本出现乱码"+text+":"+driver.getCurrentUrl()+element.locator);
      }
    }else {
      Logger.error("文本内容为空"+text+":"+driver.getCurrentUrl()+element.locator);
    }*/
    return text;
  }
  /**判断字符串是否是英文,汉字,数字*/
  public  boolean isMatcheMessy (String text){
    System.getProperty("MessRegular");
    if (!text.matches(System.getProperty("MessRegular"))) {
      return true;
    } else {
      return false;
    }
  }
  public String getCssValue(String propertyName)
  {
    return getWebElement().getCssValue(propertyName);
  }

  private void twinkleElement(String locator, String type)
  {
    try
    {
      WebElement e = this.driver.findElement(getBy(locator, type));
      ((JavascriptExecutor)driver).executeScript(
              "arguments[0].style.border = '4px solid red'", new Object[]{e});
      Thread.sleep(100L);
      ((JavascriptExecutor)driver).executeScript(
              "arguments[0].style.border = 'none'", new Object[]{e});
    } catch (NoSuchElementException e) {
      JDListenerAdapter.locator = null;
      return;
    }
    catch (Exception localException)
    {
    }
  }

  public void lightElement(String locator, String type)
  {
    if (locator == null)
      return;
    try
    {
      WebElement e = this.driver.findElement(getBy(locator, type));
      ((JavascriptExecutor)driver).executeScript(
              "arguments[0].style.border = '4px solid red'", new Object[]{e});
    }
    catch (Exception localException)
    {
    }
  }

  public WebElement getWebElement()
  {
    return this.driver.findElement(getBy());
  }

  public List<WebElement> getWebElementList()
  {
    return this.driver.findElements(getBy());
  }

  private By getBy()
  {
    this.element = ((Element)this);
    JDListenerAdapter.locator = this.element.locator;
    JDListenerAdapter.type = this.element.type;
    twinkleElement(this.element.locator, this.element.type);
    return getBy(this.element.locator, this.element.type);
  }

  public Element getElement(String key)
  {
    Element el = (Element)this;
    String xpath = el.locator.replace(" ", "").trim();
    if ((xpath.contains("\"\"") | xpath.contains("''"))) {
      if (xpath.contains("\"\""))
        xpath = xpath.substring(0, xpath.indexOf("\"\"") + 1) + el.get(key) + xpath.substring(xpath.indexOf("\"\"") + 1);
      else {
        xpath = xpath.substring(0, xpath.indexOf("''") + 1) + el.get(key) + xpath.substring(xpath.indexOf("''") + 1);
      }
    }
    Element element = (Element)el.clone();
    element.locator = xpath;
    return element;
  }

  public Element getElementByName(String name)
  {
    Element el = (Element)this;
    String xpath = el.locator.replace(" ", "").trim();
    if ((xpath.contains("\"\"") | xpath.contains("''"))) {
      if (xpath.contains("\"\""))
        xpath = xpath.substring(0, xpath.indexOf("\"\"") + 1) + name + xpath.substring(xpath.indexOf("\"\"") + 1);
      else {
        xpath = xpath.substring(0, xpath.indexOf("''") + 1) + name + xpath.substring(xpath.indexOf("''") + 1);
      }
    }
    Element element = (Element)el.clone();
    element.locator = xpath;
    return element;
  }

  public Element getElementByIndex(int index)
  {
    Element el = (Element)this;
    String xpath = el.locator.replace(" ", "").trim();
    if (xpath.contains("[]")) {
      xpath = xpath.substring(0, xpath.indexOf("[]") + 1) + index + xpath.substring(xpath.indexOf("[]") + 1);
    }
    Element element = (Element)el.clone();
    element.locator = xpath;
    return element;
  }
  public Element getElementByIndex(String index)
  {
    Element el = (Element)this;
    String xpath = el.locator.replace(" ", "").trim();
    if (xpath.contains("[]")) {
      xpath = xpath.substring(0, xpath.indexOf("[]") + 1) + index + xpath.substring(xpath.indexOf("[]") + 1);
    }
    Element element = (Element)el.clone();
    element.locator = xpath;
    return element;
  }

  private By getBy(String locator, String type)
  {
    if (type == null) {
      return By.xpath(locator);
    }
    type = type.toLowerCase();
    if (type.equals("xpath"))
    {
      return By.xpath(locator);
    }if (type.equals("id"))
    {
      return By.id(locator);
    }if ((type.equals("linktext")) || (type.equals("link")))
    {
      return By.linkText(locator);
    }if (type.equals("partiallinktext"))
    {
      return By.partialLinkText(locator);
    }if (type.equals("name"))
    {
      return By.name(locator);
    }if (type.equals("tagname"))
    {
      return By.tagName(locator);
    }if (type.equals("classname"))
    {
      return By.className(locator);
    }if ((type.equals("cssselector")) || (type.equals("css")))
    {
      return By.cssSelector(locator);
    }

    return By.xpath(locator);
  }

  public WebElement getCellDataElement(int row, int column, String tagName, int index)
  {
    List<WebElement> e = getCellDataElementList(row, column, 
      tagName);
    if (e.size() > 0) {
      if (index <= 0)
        index = 0;
      else if (index >= e.size())
        index = e.size() - 1;
      else {
        index--;
      }
      return (WebElement)e.get(index);
    }
    return null;
  }

  public List<WebElement> getCellDataElementList(int row, int column, String tagName)
  {
    return getCellDataElementList(row, column, tagName, false);
  }

  public List<WebElement> getCellDataElementList(int row, int column, String tagName, boolean filterHideTag)
  {
    if (filterHideTag) {
      return filterHideTag(getCellElement(row, column).findElements(
        By.xpath("descendant::" + tagName)));
    }
    return getCellElement(row, column).findElements(
      By.xpath("descendant::" + tagName));
  }

  public int getRowNum()
  {
    return getRowElementList().size();
  }

  public int getCellNum(int row)
  {
    return getCellElementList(row).size();
  }

  public List<WebElement> getRowElementList()
  {
    return getRowElementList(false);
  }

  public List<WebElement> getRowElementList(boolean filterHideTag)
  {
    WebElement parent = getWebElement();
    if (filterHideTag) {
      return filterHideTag(parent.findElements(By.xpath("child::tr")));
    }
    return parent.findElements(By.xpath("child::tr"));
  }

  public WebElement getCellElement(int row, int column)
  {
    List<WebElement> e = getCellElementList(row);
    if (e.size() > 0) {
      if (column <= 0)
        column = 0;
      else if (column >= e.size())
        column = e.size() - 1;
      else {
        column--;
      }
      return e.get(column);
    }
    return null;
  }

  public List<WebElement> getCellElementList(int row)
  {
    return getCellElementList(row, false);
  }

  public List<WebElement> getCellElementList(int row, boolean fiterHideTag)
  {
    List<WebElement> e = getRowElementList();
    if (e.size() > 0) {
      if (row <= 0)
        row = 0;
      else if (row >= e.size())
        row = e.size() - 1;
      else {
        row--;
      }
      if (fiterHideTag) {
        int i = filterHideTag(
          ((WebElement)e.get(row)).findElements(By.xpath("child::th"))).size();
        if (i != 0) {
          return filterHideTag(((WebElement)e.get(row)).findElements(
            By.xpath("child::th")));
        }
        return filterHideTag(((WebElement)e.get(row)).findElements(
          By.xpath("child::td")));
      }

      int i = ((WebElement)e.get(row)).findElements(By.xpath("child::th")).size();
      if (i != 0) {
        return ((WebElement)e.get(row)).findElements(
          By.xpath("child::th"));
      }
      return ((WebElement)e.get(row)).findElements(
        By.xpath("child::td"));
    }

    return null;
  }

  private List<WebElement> filterHideTag(List<WebElement> list)
  {
    List<WebElement> li = new ArrayList<WebElement>();
    if (list.size() > 0) {
      for (int i = 0; i < list.size(); i++) {
        if (((WebElement)list.get(i)).getAttribute("type") != null)
        {
          if (((WebElement)list.get(i)).getAttribute("type")
            .equalsIgnoreCase("hidden")) {
            continue;
          }
          li.add((WebElement)list.get(i));
        }
        else if (((WebElement)list.get(i)).isDisplayed()) {
          li.add((WebElement)list.get(i));
        }
      }
    }
    return li;
  }
}