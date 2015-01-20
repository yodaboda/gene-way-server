package com.nutrinfomics.geneway.server.alert.message;

import com.nutrinfomics.geneway.server.alert.EmailAlert;
import com.nutrinfomics.geneway.server.domain.contact.PhoneNumber;

public class SMSAlertEmailMessage extends EmailAlertEmailMessage {

	public SMSAlertEmailMessage(EmailAlert emailAlert) {
		super(emailAlert);
		
	}
	@Override
	protected String getSubject(){
		String phonenumber = getAlert().getCustomer().getDevice().getPhonenumber();
		if(phonenumber != null) return phonenumber;
		
		for(PhoneNumber number : getAlert().getCustomer().getContactInformation().getPhoneNumbers()){
			if(number.getDescription().equals("mobile")){
				String phoneNm = number.getNumber();
				phoneNm = phoneNm.replace("-", "");
				if(phoneNm.length() == 10 || phoneNm.substring(0, 5).contains("972")){
					return phoneNm;
				}
			}
		}
		
		return null;
	}
	@Override
	protected String getRecipient(){
		return "sms.gene.way@gmail.com";
	}

	
}