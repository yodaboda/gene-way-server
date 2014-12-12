package com.nutrinfomics.geneway.server.domain.device;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.nutrinfomics.geneway.server.data.UserMapperServices;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

@Entity
@Table(indexes = { @Index(columnList = "sid") })
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
}
