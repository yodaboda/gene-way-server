package com.nutrinfomics.geneway.server.domain.contact;

import javax.persistence.Entity;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class PhoneNumber extends EntityBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1507458874115679899L;
	private String number;
	private String description;

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
