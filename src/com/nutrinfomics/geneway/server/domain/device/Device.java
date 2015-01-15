package com.nutrinfomics.geneway.server.domain.device;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

@Entity
public class Device extends EntityBase implements Serializable{
	private String uuid;
	private String phonenumber;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="device")
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
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getPhonenumber(){
		return phonenumber;
	}

}
