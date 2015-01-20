package com.nutrinfomics.geneway.server.alert;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.alert.message.AbstractEmailMessage;
import com.nutrinfomics.geneway.server.alert.message.EmailAlertEmailMessage;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class EmailAlert extends AbstractAlert {

	protected AbstractEmailMessage abstractMessage;
	
	public EmailAlert(Customer customer) {
		super(customer);
		
		abstractMessage = new EmailAlertEmailMessage(this);
	}

	@Override
	public void remind() {
		try {
			abstractMessage.generateAndSendEmail();//"0587555520"
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void cancel() {
		abstractMessage = null;
	}


}
