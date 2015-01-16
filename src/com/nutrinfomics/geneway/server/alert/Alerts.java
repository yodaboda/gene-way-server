package com.nutrinfomics.geneway.server.alert;

import java.util.ArrayList;
import java.util.List;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.nutrinfomics.geneway.server.alert.ScheduledAlert.AlertType;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

public class Alerts {
	private static Alerts alerts;
	
	public static Alerts getInstance(){
		if(alerts == null){
			synchronized(Alerts.class){
				if(alerts == null){
					alerts = new Alerts();
				}
			}
		}
		return alerts;
	}
	
	
	private Alerts(){
		try {
			DetectorFactory.loadProfile("/home/firas/Documents/gene-way-workspace/langdetect/profiles");
			List<String> langs = DetectorFactory.getLangList();
			for(String lang : langs) System.out.println(lang);
		} catch (LangDetectException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public UserAlert createAlert(Customer customer, Snack snack){
		List<AlertType> alertTypes = new ArrayList<>();
 		if(customer.getPlan().getPlanPreferences().isEmailAlerts()){
 			alertTypes.add(AlertType.EMAIL);
		}
		if(customer.getPlan().getPlanPreferences().isSmsAlerts()){
			alertTypes.add(AlertType.SMS);
		}

		double inHours = customer.getPlan().getPlanPreferences().getSnackTimes().getTimeBetweenSnacks();

		return new ScheduledAlert(customer, inHours, snack, alertTypes);
	}


	public static UserAlert create(Customer customer, AlertType alertType) {
		switch(alertType){
			case EMAIL: return new EmailAlert(customer);
			case SMS: return new SMSAlert(customer);
			default: return null;
		}
	}
}
