package com.nutrinfomics.geneway.server.alert;

import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

public class SMSAlert extends AbstractAlert {

	public SMSAlert(Customer customer, Snack snack, double inHours) {
		super(customer, snack, inHours);
	}

	protected String getSubject(){
		return getCustomer().getDevice().getPhonenumber();
	}
	protected String getRecipient(){
		return "sms.gene.way@gmail.com";
	}

	
}
