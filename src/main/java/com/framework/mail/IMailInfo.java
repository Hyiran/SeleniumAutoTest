package com.framework.mail;

import java.util.List;
import java.util.Properties;

public abstract interface IMailInfo
{
  public abstract String getServerHost();

  public abstract String getServerPort();

  public abstract String getFromAddress();

  public abstract String getToAddress();

  public abstract String getUserName();

  public abstract String getPassword();

  public abstract Boolean getValidate();

  public abstract String getSubject();

  public abstract String getContent();

  public abstract List<String> getAttachment();

  public abstract void clearAttachment();

  public abstract String getContentType();

  public abstract Properties getProperties();
}