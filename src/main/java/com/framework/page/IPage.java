package com.framework.page;

import java.util.HashMap;

public abstract interface IPage
{
  public abstract void init(HashMap<String, Object> paramHashMap);
}