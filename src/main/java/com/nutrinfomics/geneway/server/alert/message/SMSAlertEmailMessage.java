package com.nutrinfomics.geneway.server.alert.message;

import com.nutrinfomics.geneway.server.alert.EmailAlert;
import com.nutrinfomics.geneway.server.domain.contact.PhoneNumber;

public class SMSAlertEmailMessage extends EmailAlertEmailMessage {
	public SMSAlertEmailMessage(EmailAlert emailAlert) {
		super(emailAlert, null);
	}
	@Override
	protected String getSubject(){
		String phonenumber = getAlert().getCustomer().getContactInformation().getRegisteredPhoneNumber();
		return phonenumber;
//		if(phonenumber != null) return phonenumber;
//		
		
		//because of lazy loading cannot do the following
//		for(PhoneNumber number : getAlert().getCustomer().getContactInformation().getPhoneNumbers()){
//			if(number.getDescription().equals("mobile")){
//				String phoneNm = number.getNumber();
//				phoneNm = phoneNm.replace("-", "");
//				if(phoneNm.length() == 10 || phoneNm.substring(0, 5).contains("972")){
//					return phoneNm;
//				}
//			}
//		}
		
//		return null;
	}
	@Override
	protected String getRecipient(){
		return "sms.gene.way@gmail.com";
	}

	
}
