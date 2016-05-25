package com.framework.mail;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class JDMailInfo
  implements IMailInfo
{
  private String ServerHost;
  private String ServerPort;
  private String FromAddress;
  private String ToAddress;
  private String UserName;
  private String Password;
  private boolean Validate;
  private String Subject;
  private String ContentType;
  private String Content;
  private ArrayList<String> Attachment = new ArrayList<String>();

  private Properties p = new Properties();

  private final String ENCODING = "UTF-8";

  private final String HTMLPATH = "mail.html.vm";

  private final String TEXTPATH = "mail.text.vm";

  public JDMailInfo()
  {
    this.ServerHost = System.getProperty("Mail.ServerHost", "192.168.193.82");
    this.ServerPort = System.getProperty("Mail.ServerPort", "25");
    this.FromAddress = System.getProperty("Mail.FromAddress", "");
    this.ToAddress = System.getProperty("Mail.ToAddress", "");
    this.UserName = System.getProperty("Mail.UserName", "");
    this.Password = System.getProperty("Mail.Password", "");
    this.Validate = System.getProperty("Mail.Validate", "true").equalsIgnoreCase("true");
    this.Subject = ("[" + System.getProperty("Project.Name") + "]" + System.getProperty("Mail.Subject", "自动化邮件主题"));
    Calendar c = Calendar.getInstance();
    SimpleDateFormat f = new SimpleDateFormat("[yyyy年MM月dd日]");
    this.Subject = (f.format(c.getTime()) + this.Subject);
    this.ContentType = System.getProperty("Mail.ContentType", "html");
    this.p.put("mail.smtp.host", this.ServerHost);
    this.p.put("mail.smtp.port", this.ServerPort);
    this.p.put("mail.smtp.auth", this.Validate ? "true" : "false");
    this.Content = formatMailContentFromTemplate();
  }

  public String formatMailContentFromTemplate()
  {
    String line = "";

    return line;
  }

  private String getDuringTime() {
    NumberFormat DURATION_FORMAT = new DecimalFormat("#0.000");
    Date end = new Date();
    StringBuilder time = new StringBuilder();
    long diff = end.getTime() - Long.parseLong(System.getProperty("TestMiliTimeStamp"));
    long hour = diff / 3600000L;
    long min = diff / 60000L - hour * 60L;
    String s = DURATION_FORMAT.format(diff / 1000.0D - hour * 60L * 60L - min * 60L);
    if (hour > 0L) {
      time.append(hour + ":");
    }
    if ((hour > 0L) || (min > 0L)) {
      time.append(min + ":");
    }
    time.append(s + "s");
    return time.toString();
  }

  public String getServerHost()
  {
    return this.ServerHost;
  }

  public void setServerHost(String serverHost) {
    this.ServerHost = serverHost;
    this.p.put("mail.smtp.host", serverHost);
  }

  public String getServerPort()
  {
    return this.ServerPort;
  }

  public void setServerPort(String serverPort) {
    this.ServerPort = serverPort;
    this.p.put("mail.smtp.port", serverPort);
  }

  public String getFromAddress()
  {
    return this.FromAddress;
  }

  public void setFromAddress(String fromAddress) {
    this.FromAddress = fromAddress;
  }

  public String getToAddress()
  {
    return this.ToAddress;
  }

  public void setToAddress(String toAddress) {
    this.ToAddress = toAddress;
  }

  public String getUserName()
  {
    return this.UserName;
  }

  public void setUserName(String userName) {
    this.UserName = userName;
  }

  public String getPassword()
  {
    return this.Password;
  }

  public void setPassword(String password) {
    this.Password = password;
  }

  public Boolean getValidate()
  {
    return Boolean.valueOf(this.Validate);
  }

  public void setValidate(boolean validate) {
    this.Validate = validate;
    this.p.put("mail.smtp.auth", validate ? "true" : "false");
  }

  public String getSubject()
  {
    return this.Subject;
  }

  public void setSubject(String subject) {
    this.Subject = subject;
  }

  public String getContent()
  {
    return this.Content;
  }

  public void setContent(String content) {
    this.Content = content;
  }

  public String getContentType()
  {
    return this.ContentType;
  }

  public void setContentType(String contentType) {
    this.ContentType = contentType;
  }

  public Properties getProperties()
  {
    return this.p;
  }

  public List<String> getAttachment()
  {
    return this.Attachment;
  }

  public void addAttachment(String attachmentPath)
  {
    this.Attachment.add(attachmentPath);
  }

  public void clearAttachment()
  {
    this.Attachment.clear();
  }
}