package com.nutrinfomics.geneway.server.alert;

import com.nutrinfomics.geneway.server.domain.contact.PhoneNumber;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class SMSAlert extends EmailAlert {

	public SMSAlert(Customer customer) {
		super(customer);
	}


	
}
