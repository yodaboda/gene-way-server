package com.nutrinfomics.geneway.server.domain.device;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.nutrinfomics.geneway.server.data.UserMapperServices;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

@Entity
public class Session extends EntityBase implements Serializable{
	private String sid;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="session")
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
	
//	public static Session findSession(long id){
//		return UserMapperServices.getInstance().findSession(id);
//	}
}
