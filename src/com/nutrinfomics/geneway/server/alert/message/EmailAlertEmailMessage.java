package com.nutrinfomics.geneway.server.alert.message;

import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.alert.UserAlert;

public class EmailAlertEmailMessage extends AbstractEmailMessage {

	private UserAlert userAlert;
	private String email;
	
	public EmailAlertEmailMessage(UserAlert userAlert, String email){
		this.userAlert = userAlert;
		this.email = email;
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
//		return getAlert().getCustomer().getContactInformation().getEmails().get(0).getEmail(); //because of lazy loading cannot get email here
		return email;
	}

	@Override
	protected String getBody() {
		return ResourceBundles.getGeneWayResource("itsTimeToTakeYourMeal", getAlert().getLocale()) + "\n\r https://gene-way.com";
	}
}
