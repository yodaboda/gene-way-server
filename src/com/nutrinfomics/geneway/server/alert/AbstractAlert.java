package com.nutrinfomics.geneway.server.alert;

import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.nutrinfomics.geneway.server.alert.format.SnackFormat;
import com.nutrinfomics.geneway.server.alert.format.resources.ResourceBundles;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

abstract public class AbstractAlert implements UserAlert {

	private Customer customer;
	private Snack snack;
	private double inHours;
	private Locale locale;
	
	public AbstractAlert(Customer customer, Snack snack, double inHours){
		this.customer = customer;
		this.snack = snack;
		this.inHours = inHours;
		locale = new Locale(calcLanguage());
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				reminder();
			}
		}, (long) (inHours * 60 * 60 * 1000));
	}

	public Customer getCustomer() {
		return customer;
	}

	protected void reminder() {
		try {
			generateAndSendEmail();//"0587555520"
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected Locale getLocale(){
		return locale;
	}
	
	private String calcLanguage(){
		try{
			Detector detector = DetectorFactory.create();
			detector.append(customer.getPersonalDetails().getFirstName());
			return detector.detect();
		}
		catch(LangDetectException e){
			e.printStackTrace();
		}
		return "en";
	}
	
	abstract protected String getSubject();

	abstract protected String getRecipient();
	
	protected String getBody(){
		return ResourceBundles.getGeneWayResource("itsTimeToTakeYourMeal", getLocale()) + SnackFormat.getInstance().format(getSnack(), getLocale());
	}

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

	public Snack getSnack() {
		return snack;
	}

}
