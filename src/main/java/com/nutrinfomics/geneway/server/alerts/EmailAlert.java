package com.nutrinfomics.geneway.server.alerts;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geneway.alerts.AlertMechanism;
import com.nutrinfomics.geneway.server.domain.EntityBase;

/**
 * Alerts to be sent to users through email.
 * @author Firas Swidan
 *
 */
public class EmailAlert extends EntityBase implements Alert{
	/**
	 * Logger for unexpected events.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	/**
	 * 
	 */
	private static final long serialVersionUID = -2307422999927307233L;

	/**
	 * The mechanism for sending alert reminders.
	 */
	private AlertMechanism alertMechanism;
	
	@Inject
	public EmailAlert(AlertMechanism alertMechanism) {
		this.alertMechanism = alertMechanism;
	}

	/**
	 * Remind user of recommended action.
	 */
	@Override
	public void remind() {
		try {
			this.getAlertMechanism().send();
		} catch (MessagingException e) {
			LOGGER.log(Level.FATAL, e.toString(), e);
		}
	}

	private AlertMechanism getAlertMechanism() {
		return alertMechanism;
	}
}
