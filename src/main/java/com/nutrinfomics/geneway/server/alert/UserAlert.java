package com.nutrinfomics.geneway.server.alert;

import java.util.Locale;

import com.nutrinfomics.geneway.server.domain.customer.Customer;

public interface UserAlert {
	public void cancel();
	public void remind();
	public Customer getCustomer();
	public Locale getLocale();
}
