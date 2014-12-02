package com.nutrinfomics.geneway.server.domain.customer;

import java.io.Serializable;
import java.util.Date;

import com.nutrinfomics.geneway.server.domain.ModelObject;

public class PersonalDetails implements Serializable{
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	private String email;
	private String firstName;
	private String lastName;
	private Customer customer;
	private Date birthday;
}
