package com.nutrinfomics.geneway.server.alerts;

import com.geneway.alerts.SMSAlert;
import com.geneway.alerts.UserAlert;
import com.geneway.alerts.mechanism.SMSOverEmailAlertMechanism;
import com.geneway.alerts.recipient.EmailAlertRecipient;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.alerts.codeSMS.GeneWayCodeSMSAlertLocalization;
import com.nutrinfomics.geneway.server.alerts.codeSMS.GeneWayCodeSMSAlertMessage;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class GeneWaySMSAlert extends EntityBase implements UserAlert {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7017431098098326348L;
	private SMSAlert smsAlert;
	public GeneWaySMSAlert(String phoneNumber) {
		smsAlert = new SMSAlert(new SMSOverEmailAlertMechanism(new GeneWayEmailAlertMessage(), 
																new EmailAlertRecipient("sms.gene.way@gmail.com"), 
																new GeneWayEmailAlertLocalization(new ResourceBundles(), new Utils().getLocale(new RequestUtils())),
																phoneNumber));
	}
	@Override
	public void cancel() {
		smsAlert.cancel();
	}
	@Override
	public void remind() {
		smsAlert.remind();
	}

}
