package com.nutrinfomics.geneway.server.alerts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.geneway.alerts.AbstractAlert;
import com.geneway.alerts.EmailAlert;
import com.geneway.alerts.SMSAlert;
import com.geneway.alerts.UserAlert;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackHistory;
import com.nutrinfomics.geneway.server.requestfactory.request.PlanService;
import com.nutrinfomics.geneway.shared.SnackStatus;

public class ScheduledAlert extends EntityBase implements UserAlert {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6756667893271410965L;
	static private final ScheduledExecutorService scheduler =
		     Executors.newScheduledThreadPool(1);

	public enum AlertType{
		EMAIL, SMS, PUSH_NOTIFICATION
	}
	
	private List<UserAlert> alerts = new ArrayList<>();
	private ScheduledFuture<?> scheduled;
	private Snack snack;

	public ScheduledAlert(Customer customer, double inHours, Snack snack, List<AlertType> alertTypes, String email ) {
		this.snack = snack;
		for(AlertType alertType : alertTypes){
			alerts.add(create(customer, alertType, email));
		}
		
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				remind();
			}
		};
		scheduled = scheduler.schedule(runnable, (long) (inHours * 60), TimeUnit.MINUTES);
	}

	public UserAlert create(Customer customer, AlertType alertType, String email) {
		switch(alertType){
			case EMAIL: return new GeneWayEmailAlert(email);
			case SMS: return new GeneWaySMSAlert(customer.getContactInformation().getRegisteredPhoneNumber());
			default: return null;
		}
	}
	
	@Override
	public void cancel() {
		scheduled.cancel(false);
		Alerts.getInstance().removeSnackAlert(snack);
	}

//	@Transactional
	@Override
	public void remind() {
		for(UserAlert alert : alerts){
			alert.remind();
		}
		Alerts.getInstance().removeSnackAlert(snack);

//		SnackHistory snackHistory = new SnackHistory();
//		snackHistory.setEatenSnack(snack);
//		snackHistory.setPlannedSnack(snack);
//		snackHistory.setStatus(SnackStatus.CONSUMED);
//		Date timestamp = new Date();
//		snackHistory.setTimestamp(timestamp);
//		//TODO: need to modify date string and timezone offset computation for international clients
//		String dateString = SnackHistory.getDateString(timestamp, timestamp.getTimezoneOffset());
//		snackHistory.setDayString(dateString);
//		snackHistory.setTimeZoneDiff(timestamp.getTimezoneOffset());
//		snackHistory.setCustomer(getCustomer());
//		
//		em.get().persist(snackHistory);
//		
//		new PlanService().getNextSnack(getCustomer().getSession(), dateString);
		
	}
}
