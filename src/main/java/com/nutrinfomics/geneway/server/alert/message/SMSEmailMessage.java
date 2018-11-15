package com.nutrinfomics.geneway.server.alert.message;

import java.util.Locale;

import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.Utils;

public class SMSEmailMessage extends AbstractSMSEmailMessage {

	private String code;
	private String nickName;
	
	public SMSEmailMessage(String phoneNumber, String nickName, String code) {
		super(phoneNumber);
		this.code = code;
		this.nickName = nickName;
	}
	
	@Override
	protected String getBody() {
		Locale locale = new Utils().getLocale(new RequestUtils());
		return ResourceBundles.getGeneWayResource("dear", locale) + " " + nickName + ". " + 
				ResourceBundles.getGeneWayResource("yourCodeIs", locale) + ": " + code;
	}

}
