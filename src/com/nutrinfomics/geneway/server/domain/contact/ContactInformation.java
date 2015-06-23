package com.nutrinfomics.geneway.server.domain.contact;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class ContactInformation extends EntityBase{
	
	@OneToMany(fetch=FetchType.LAZY, cascade = {CascadeType.ALL})
	private List<PhoneNumber> phoneNumbers = new ArrayList<>();
	
	@OneToMany(fetch=FetchType.LAZY, cascade = {CascadeType.ALL})
	private List<Email> emails = new ArrayList<>();

	@Pattern(regexp="[0-9]{10,20}", message="{contactInformation.phonenumber.pattern.message}")
//	@Size(min=10, max=20, message="{device.phonenumber.size.message}")
	@Column(nullable = false, unique = true)
	private String phonenumber;
	
	public String getRegisteredPhoneNumber(){
		return phonenumber;
	}
	
	public void setRegisteredPhoneNumber(String registeredPhoneNumber){
		phonenumber = registeredPhoneNumber;
	}
	
	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	public List<Email> getEmails() {
		return emails;
	}
	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}
}
