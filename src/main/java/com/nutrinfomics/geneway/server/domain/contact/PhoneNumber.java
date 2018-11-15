package com.nutrinfomics.geneway.server.domain.contact;

import javax.persistence.Entity;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class PhoneNumber extends EntityBase{
	private String number;
	private String description;
	public PhoneNumber(){
		
	}
	
	public void setNumber(String number){
		this.number = number;
	}
	
	public String getNumber(){
		return number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
