package com.framework.data.provider;

/**
 * Created by caijianmin on 2016/5/25.
 */
public interface IData {
    public abstract Object[][] getData(String paramString1, String paramString2);

    public abstract Object[][] getData(String paramString1, String paramString2, int paramInt);

    public abstract Object[][] getData(String paramString1, String paramString2, int paramInt1, int paramInt2);
}
