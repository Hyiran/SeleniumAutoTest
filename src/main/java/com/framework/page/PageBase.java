package com.framework.page;

import com.framework.webdriver.RunTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by caijianmin on 2016/6/6.
 */
public abstract class PageBase implements IPage {

    WebDriver driver= RunTest.getDriver();

    @Override
    public abstract void init(HashMap<String, Object> paramHashMap) ;

    /**
     * 截取整个浏览器页面
     */
    public void screenShot(String name) {
        String path = System.getProperty("user.dir") + "/test-output/screenshot";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        File screenShotFile =  ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenShotFile, new File(path + "/" + name + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 只接当前屏幕图片
     */
    public  String takeScreenshot( String name) {
        String path = System.getProperty("user.dir") + "/test-output/screenshot";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            path = path + "/" + name+ ".jpg";
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage screenshot = new Robot().createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));
            File f = new File(path);

            ImageIO.write(screenshot, "jpg", f);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return path;
    }

    public void screenShot() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        screenShot(df.format(new Date()));
        takeScreenshot(df.format(new Date()));
    }
}
