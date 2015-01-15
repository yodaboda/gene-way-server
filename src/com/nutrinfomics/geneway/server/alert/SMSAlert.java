package com.nutrinfomics.geneway.server.alert;

import com.nutrinfomics.geneway.server.domain.contact.PhoneNumber;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

public class SMSAlert extends AbstractAlert {

	public SMSAlert(Customer customer, Snack snack, double inHours) {
		super(customer, snack, inHours);
	}

	protected String getSubject(){
		String phonenumber = getCustomer().getDevice().getPhonenumber();
		if(phonenumber != null) return phonenumber;
		
		for(PhoneNumber number : getCustomer().getContactInformation().getPhoneNumbers()){
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
	protected String getRecipient(){
		return "sms.gene.way@gmail.com";
	}

	
}
