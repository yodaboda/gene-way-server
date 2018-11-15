package com.nutrinfomics.geneway.server.alert.message;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

abstract public class AbstractEmailMessage {

	abstract protected String getSubject();

	abstract protected String getRecipient();

	abstract protected String getBody();
	
	public void generateAndSendEmail() throws AddressException, MessagingException {

		String subject = getSubject();
		String body = getBody();
		String recipient = getRecipient();
		//Step1		
//		System.out.println("\n 1st ===> setup Mail Server Properties..");
		Properties mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
//		System.out.println("Mail Server Properties have been setup successfully..");

		//Step2		
//		System.out.println("\n\n 2nd ===> get Mail Session..");
		Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		MimeMessage generateMailMessage = new MimeMessage(getMailSession);
//		generateMailMessage.setHeader("Content-Type", "text/plain; charset=UTF-8");
		generateMailMessage.addRecipient(RecipientType.TO, new InternetAddress(recipient));
//		generateMailMessage.addRecipient(RecipientType.CC, new InternetAddress("nutrinfomics@gmail.com"));
		generateMailMessage.setSubject(subject);
		generateMailMessage.setText(body);
		
//		generateMailMessage.setContent(emailBody, "text/plain; charset=UTF-8");
//		System.out.println("Mail Session has been created successfully..");

		//Step3		
//		System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");

		// Enter your correct gmail UserID and Password (XXXarpitshah@gmail.com)
		transport.connect("smtp.gmail.com", "sms.gene.way@gmail.com", "r8B0iR7M");
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}

}
