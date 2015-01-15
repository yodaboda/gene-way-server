package com.nutrinfomics.geneway.server.alert;

import com.nutrinfomics.geneway.server.alert.format.SnackFormat;
import com.nutrinfomics.geneway.server.alert.format.resources.ResourceBundles;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

public class EmailAlert extends AbstractAlert {

	public EmailAlert(Customer customer, Snack snack, double inHours) {
		super(customer, snack, inHours);
	}

	protected String getSubject(){
		return ResourceBundles.getGeneWayResource("itsTimeToTakeYourMealTitle", getLocale());
	}
	protected String getRecipient(){
		return getCustomer().getContactInformation().getEmails().get(0).getEmail();
	}

}
