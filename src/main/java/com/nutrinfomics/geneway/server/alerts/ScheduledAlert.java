package com.nutrinfomics.geneway.server.alerts;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.persistence.Transient;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.nutrinfomics.geneway.server.domain.EntityBase;

public class ScheduledAlert extends EntityBase implements Alert {

	/**
	 * Logger for unexpected events.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * 
	 */
	private static final long serialVersionUID = -6756667893271410965L;

	
	@Transient
	private Alert alert;
	@Transient
	private ScheduledFuture<?> scheduled;
	@Transient
	private ScheduledExecutorService schedulerService;

	@Inject
	public ScheduledAlert(Alert alert, ScheduledExecutorService schedulerService){
		this.alert = alert;
		this.schedulerService = schedulerService;
	}

	public void schedule(double inHours){
		Runnable runnable = () -> remind();
		scheduled = schedulerService.schedule(runnable, (long) (inHours * 60), TimeUnit.MINUTES);

	}
	
//	public ScheduledAlert(Customer customer, double inHours, Snack snack, List<AlertType> alertTypes, String email ) {
//		this.snack = snack;
//		for(AlertType alertType : alertTypes){
//			alerts.add(create(customer, alertType, email));
//		}
//		
//		Runnable runnable = new Runnable() {
//			
//			@Override
//			public void run() {
//				remind();
//			}
//		};
//		scheduled = scheduler.schedule(runnable, (long) (inHours * 60), TimeUnit.MINUTES);
//	}
//
//	public UserAlert create(Customer customer, AlertType alertType, String email) {
//		switch(alertType){
//			case EMAIL: return new GeneWayEmailAlert(email);
//			case SMS: return new GeneWaySMSAlert(customer.getContactInformation().getRegisteredPhoneNumber());
//			default: return null;
//		}
//	}
	
	public void cancel() {
		if(scheduled != null) {
			scheduled.cancel(false);			
		}
		else {
			LOGGER.log(Level.INFO, "Attempting to cancel a null scheduled field");
		}
//		Alerts.getInstance().removeSnackAlert(snack);
	}

//	@Transactional
	@Override
	public void remind() {
//		for(Alert alert : alerts){
			alert.remind();
//		}
//		Alerts.getInstance().removeSnackAlert(snack);

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
