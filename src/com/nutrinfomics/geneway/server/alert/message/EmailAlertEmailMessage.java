package com.nutrinfomics.geneway.server.alert.message;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.alert.UserAlert;

public class EmailAlertEmailMessage extends AbstractEmailMessage {

	private UserAlert userAlert;
	
	public EmailAlertEmailMessage(UserAlert userAlert){
		this.userAlert = userAlert;
	}
	
	
	protected UserAlert getAlert(){
		return userAlert;
	}
	
	@Override
	protected String getSubject() {
		return ResourceBundles.getGeneWayResource("itsTimeToTakeYourMealTitle", getAlert().getLocale());
	}

	@Override
	protected String getRecipient() {
		return getAlert().getCustomer().getContactInformation().getEmails().get(0).getEmail();
	}

	@Override
	protected String getBody() {
		return ResourceBundles.getGeneWayResource("itsTimeToTakeYourMeal", getAlert().getLocale()) + "\n\r https://gene-way.com";
	}
}
