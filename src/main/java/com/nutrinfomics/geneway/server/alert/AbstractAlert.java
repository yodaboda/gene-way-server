package com.nutrinfomics.geneway.server.alert;

import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.alert.format.SnackFormat;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

abstract public class AbstractAlert extends EntityBase implements UserAlert {
	
	private Customer customer;
	private Locale locale;
	
	public AbstractAlert(Customer customer){
		this.customer = customer;
		locale = new Utils().getLocale(new RequestUtils());
//		locale = new Locale(calcLanguage());

	}
	public Customer getCustomer() {
		return customer;
	}


	public Locale getLocale(){
		return locale;
	}
	
//	private String calcLanguage(){
//		try{
//			Detector detector = DetectorFactory.create();
//			detector.append(customer.getPersonalDetails().getFirstName());
//			return detector.detect();
//		}
//		catch(LangDetectException e){
//			e.printStackTrace();
//		}
//		return "en";
//	}

}
