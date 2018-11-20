package com.nutrinfomics.geneway.server.alerts;

import com.geneway.alerts.EmailAlert;
import com.geneway.alerts.UserAlert;
import com.geneway.alerts.mechanism.EmailAlertMechanism;
import com.geneway.alerts.recipient.EmailAlertRecipient;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class GeneWayEmailAlert extends EntityBase implements UserAlert {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4054041662432253171L;
	private EmailAlert emailAlert;
	public GeneWayEmailAlert(String email) {
		emailAlert = new EmailAlert(new EmailAlertMechanism(new GeneWayEmailAlertMessage(), 
									new EmailAlertRecipient(email), 
									new GeneWayEmailAlertLocalization(new ResourceBundles(), new Utils().getLocale(new RequestUtils()))));
	}
	@Override
	public void cancel() {
		emailAlert.cancel();
	}
	@Override
	public void remind() {
		emailAlert.remind();
	}

}
