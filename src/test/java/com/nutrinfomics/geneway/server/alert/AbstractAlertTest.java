package com.nutrinfomics.geneway.server.alert;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.junit.Test;

import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class AbstractAlertTest {
	
	private AbstractAlert buildAbstractAlert(Customer c){
		return new AbstractAlert(c) {
			
			@Override
			public void remind() {
				
			}
			
			@Override
			public void cancel() {
				
			}
		};
	}
	
	@Test
	public void testGetCustomer() {
		Customer mockedCustomer = mock(Customer.class);
		AbstractAlert abstractAlert = buildAbstractAlert(mockedCustomer);
		assertEquals(mockedCustomer, abstractAlert.getCustomer());
	}

	@Test
	public void testGetCustomerNull() {
		AbstractAlert abstractAlert = buildAbstractAlert(null);
		assertEquals(null, abstractAlert.getCustomer());
	}

	@Test
	public void testGetLocale() {
		Customer mockedCustomer = mock(Customer.class);
		AbstractAlert abstractAlert = buildAbstractAlert(mockedCustomer);
		assertEquals(Locale.ENGLISH, abstractAlert.getLocale());
	}

}
