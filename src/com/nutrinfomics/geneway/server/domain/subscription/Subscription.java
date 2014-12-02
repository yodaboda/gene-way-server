package com.nutrinfomics.geneway.server.domain.subscription;

import java.util.Date;

import com.nutrinfomics.geneway.server.domain.ModelObject;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class Subscription extends ModelObject{
	Date start;
	Date end;
	Customer customer;

	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
