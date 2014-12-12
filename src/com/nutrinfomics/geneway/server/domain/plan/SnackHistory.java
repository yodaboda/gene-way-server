package com.nutrinfomics.geneway.server.domain.plan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.shared.SnackStatus;

@Entity
@Table(indexes = { @Index(columnList = "snack, dayString", unique = true) })
public class SnackHistory extends EntityBase{

	@Id
	@OneToOne(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
	private Snack snack;

	@Id
	private String dayString;

	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;
	
	private int timeZoneDiff;
	
	@Enumerated(EnumType.STRING)
	private SnackStatus status;
	
	public static boolean isSnackMarked(Snack snack, String dayString) {
		EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();
		TypedQuery<SnackHistory> query = entityManager.createQuery("SELECT s FROM SnackHistory h WHERE snack = :snack AND dayString = :dayString", SnackHistory.class).setParameter("snack", snack).setParameter("dayString", dayString);
		try{
			SnackHistory snackHistory = query.getSingleResult();
			return snackHistory.getStatus() == SnackStatus.CONSUMED || snackHistory.getStatus() == SnackStatus.SKIPPED;
		}
		catch(Exception e){
			return false;
		}
	}


	public Snack getSnack() {
		return snack;
	}


	public void setSnack(Snack snack) {
		this.snack = snack;
	}

	public String getDayString() {
		return dayString;
	}


	public void setDayString(String dayString) {
		this.dayString = dayString;
	}


	public Date getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	
	public int getTimeZoneDiff() {
		return timeZoneDiff;
	}

	public void setTimeZoneDiff(int timeZoneDiff) {
		this.timeZoneDiff = timeZoneDiff;
	}


	public SnackStatus getStatus() {
		return status;
	}


	public void setStatus(SnackStatus status) {
		this.status = status;
	}


	static public String getDateString(Date timestamp, int timeZoneOffset) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timestamp);
		calendar.set(Calendar.ZONE_OFFSET, timeZoneOffset);
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		formatter.setCalendar(calendar);
				
		String dateString = formatter.format(calendar.getTime());
		return dateString;
	}


	public static void markSnack(Snack currentSnack, SnackStatus snackStatus, Date date, int timeZoneOffset) {
		SnackHistory snackHistory = new SnackHistory();
		
		snackHistory.setSnack(currentSnack);
		snackHistory.setDayString(getDateString(date, timeZoneOffset));
		snackHistory.setStatus(snackStatus);
		snackHistory.setTimestamp(date);
		snackHistory.setTimeZoneDiff(timeZoneOffset);
		
		snackHistory.persist();
	}

}
