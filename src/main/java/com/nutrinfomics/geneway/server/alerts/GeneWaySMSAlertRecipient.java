package com.nutrinfomics.geneway.server.alerts;

import com.geneway.alerts.recipient.EmailAlertRecipient;

public class GeneWaySMSAlertRecipient extends EmailAlertRecipient {

	public GeneWaySMSAlertRecipient() {
		super("sms.gene.way@gmail.com");
	}

}
