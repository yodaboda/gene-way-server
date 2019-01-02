package com.nutrinfomics.geneway.server.domain.contact;

import javax.persistence.Entity;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class Email extends EntityBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -11865244538655550L;

	@org.hibernate.validator.constraints.Email
	private String email;

	private String description;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
