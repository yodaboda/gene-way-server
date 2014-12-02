package com.nutrinfomics.geneway.server.domain.device;

import java.io.Serializable;

import com.nutrinfomics.geneway.server.domain.ModelObject;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class Device extends ModelObject implements Serializable{
	private String uuid;
	private Customer customer;

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}
