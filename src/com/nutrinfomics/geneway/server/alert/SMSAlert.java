package com.nutrinfomics.geneway.server.alert;

import com.nutrinfomics.geneway.server.alert.message.SMSAlertEmailMessage;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class SMSAlert extends EmailAlert {

	public SMSAlert(Customer customer) {
		super(customer);
		abstractMessage = new SMSAlertEmailMessage(this);

	}


	
}
