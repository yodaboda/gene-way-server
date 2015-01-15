package com.nutrinfomics.geneway.server.alert;

import java.util.ArrayList;
import java.util.List;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

public class Alerts {
	private static Alerts alerts;
	
	private List<UserAlert> alertList = new ArrayList<>();
	
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
	
	public void add(Customer customer, Snack snack){
		if(customer.getPlan().getPlanPreferences().isEmailAlerts()){
			double inHours = customer.getPlan().getPlanPreferences().getSnackTimes().getTimeBetweenSnacks();
			UserAlert alert = new EmailAlert(customer, snack, inHours);
			alertList.add(alert);
		}
		if(customer.getPlan().getPlanPreferences().isSmsAlerts()){
			double inHours = customer.getPlan().getPlanPreferences().getSnackTimes().getTimeBetweenSnacks();
			UserAlert alert = new SMSAlert(customer, snack, inHours);
			alertList.add(alert);
		}
	}
}
