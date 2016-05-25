package com.framework.mail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.Date;

public class SendMail extends Authenticator
{
  private IMailInfo minfo;
  private Session sendMailSession;

  public SendMail(IMailInfo info)
  {
    this.minfo = info;
    if (this.minfo.getValidate().booleanValue()) {
      getPasswordAuthentication();
      this.sendMailSession = Session.getDefaultInstance(this.minfo.getProperties(), this);
    } else {
      this.sendMailSession = Session.getDefaultInstance(this.minfo.getProperties());
    }
  }

  public boolean sendStart()
  {
    try {
      Message mailMessage = new MimeMessage(this.sendMailSession);

      mailMessage.setFrom(new InternetAddress(this.minfo.getFromAddress()));

      String[] toAddresses = this.minfo.getToAddress().split(";");
      ArrayList adds = new ArrayList();
      for (String a : toAddresses) {
        if (!a.isEmpty()) {
          adds.add(new InternetAddress(a));
        }
      }
      InternetAddress[] addss = new InternetAddress[adds.size()];
      mailMessage.setRecipients(Message.RecipientType.TO, 
        (Address[])adds.toArray(addss));

      mailMessage.setSubject(this.minfo.getSubject());

      mailMessage.setSentDate(new Date());
      if (this.minfo.getContentType().equalsIgnoreCase("html"))
      {
        Object mainPart = new MimeMultipart();

        Object html = new MimeBodyPart();

        for (String filename : this.minfo.getAttachment()) {
          MimeBodyPart mbpFile = new MimeBodyPart();
          FileDataSource fds = new FileDataSource(filename);
          mbpFile.setDataHandler(new DataHandler(fds));
          mbpFile.setFileName(filename);

          ((Multipart)mainPart).addBodyPart(mbpFile);
        }
        this.minfo.clearAttachment();

        ((BodyPart)html).setContent(this.minfo.getContent(), "text/html; charset=utf-8");
        ((Multipart)mainPart).addBodyPart((BodyPart)html);

        mailMessage.setContent((Multipart)mainPart);
      } else {
        mailMessage.setText(this.minfo.getContent());
      }

      Transport.send(mailMessage);
      return true;
    } catch (MessagingException ex) {
      ex.printStackTrace();
    }
    return false;
  }

  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(this.minfo.getUserName(), 
      this.minfo.getPassword());
  }
}