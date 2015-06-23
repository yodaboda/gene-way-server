package com.nutrinfomics.geneway.server.alert.message;

import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.Utils;

public class SMSEmailMessage extends AbstractEmailMessage {

	private String phoneNumber;
	private String code;
	private String nickName;
	
	public SMSEmailMessage(String phoneNumber, String nickName, String code) {
		this.phoneNumber = phoneNumber;
		this.code = code;
		this.nickName = nickName;
	}
	
	@Override
	protected String getSubject() {
		return phoneNumber;
	}

	@Override
	protected String getRecipient() {
		return "sms.gene.way@gmail.com";
	}

	@Override
	protected String getBody() {
		return ResourceBundles.getGeneWayResource("dear", Utils.getLocale()) + " " + nickName + ". " + 
				ResourceBundles.getGeneWayResource("yourCodeIs", Utils.getLocale()) + ": " + code;
	}

}
