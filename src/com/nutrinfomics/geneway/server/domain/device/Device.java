package com.nutrinfomics.geneway.server.domain.device;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

@Entity
public class Device extends EntityBase implements Serializable{
	@Column(nullable=false, unique=true)
	private String uuid;
	
	@Pattern(regexp="[0-9]{10,20}", message="{device.phonenumber.pattern.message}")
//	@Size(min=10, max=20, message="{device.phonenumber.size.message}")
	@Column(nullable = false, unique = true)
	private String phonenumber;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="device")
	private Customer customer;

	private String code;

	@Temporal(TemporalType.TIMESTAMP)
	private Date codeCreation;
	
	public Date getCodeCreation() {
		return codeCreation;
	}
	public void setCodeCreation(Date codeCreation) {
		this.codeCreation = codeCreation;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
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
