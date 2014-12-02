package com.nutrinfomics.geneway.server.domain.device;

import java.io.Serializable;

import com.nutrinfomics.geneway.server.data.UserMapperServices;
import com.nutrinfomics.geneway.server.domain.ModelObject;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

public class Session extends ModelObject implements Serializable{
	private String sid;
	private Customer customer;
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
	
	public static Session findSession(long id){
		return UserMapperServices.getInstance().findSession(id);
	}
}
