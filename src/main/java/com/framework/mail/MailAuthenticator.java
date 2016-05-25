package com.framework.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator
{
  private String userName;
  private String passWord;

  public MailAuthenticator(String username, String password)
  {
    this.userName = username;
    this.passWord = password;
  }

  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(this.userName, this.passWord);
  }
}