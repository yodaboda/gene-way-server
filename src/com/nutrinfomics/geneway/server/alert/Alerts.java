package com.nutrinfomics.geneway.server.alert;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.nutrinfomics.geneway.server.alert.ScheduledAlert.AlertType;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

public class Alerts {
	private static Alerts alerts;
	
	private Map<Long, UserAlert> snackAlertMapping = new HashMap<>();
	
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
			DetectorFactory.loadProfile(System.getProperty("user.home") + "/Documents/gene-way-workspace/gene-way-app/extraDependencies/langdetect/profiles");
			List<String> langs = DetectorFactory.getLangList();
			for(String lang : langs) System.out.println(lang);
		} catch (LangDetectException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public UserAlert createAlert(Customer customer, Snack snack, boolean sameDay, String email){
		List<AlertType> alertTypes = new ArrayList<>();
 		if(customer.getPlan().getPlanPreferences().isEmailAlerts()){
 			alertTypes.add(AlertType.EMAIL);
		}
		if(customer.getPlan().getPlanPreferences().isSmsAlerts()){
			alertTypes.add(AlertType.SMS);
		}

		double inHours = customer.getPlan().getPlanPreferences().getSnackTimes().getTimeBetweenSnacks();
		if(!sameDay){
			LocalDate tomorrow = LocalDate.now().plusDays(1);
			LocalTime mealTime = LocalTime.of(8, 0, 0);
			LocalDateTime mealDateTime = LocalDateTime.of(tomorrow, mealTime);
			
			LocalDateTime now = LocalDateTime.now();
			
			Duration duration = Duration.between(now, mealDateTime);
			inHours = duration.toHours();
		}
		
		UserAlert userAlert = new ScheduledAlert(customer, inHours, snack, alertTypes, email);
		
		snackAlertMapping.put(snack.getId(), userAlert);
		
		return userAlert;
	}

	public UserAlert getSnackAlert(long l){
		return snackAlertMapping.get(l);
	}
	
	public void removeSnackAlert(Snack snack){
		snackAlertMapping.remove(snack);
	}

	public static UserAlert create(Customer customer, AlertType alertType, String email) {
		switch(alertType){
			case EMAIL: return new EmailAlert(customer, email);
			case SMS: return new SMSAlert(customer, email);
			default: return null;
		}
	}
}
