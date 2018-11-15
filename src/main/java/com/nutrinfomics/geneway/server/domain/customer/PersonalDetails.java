package com.nutrinfomics.geneway.server.domain.customer;



import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.hibernate.validator.constraints.NotBlank;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.shared.Gender;

@Entity
public class PersonalDetails extends EntityBase{

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
	public SimpleDate getBirthday() {
		return birthday;
	}
	public void setBirthday(SimpleDate birthday) {
		this.birthday = birthday;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
//	@Pattern(regexp="/^[\p{L}'][ \p{L}'-]*[\p{L}]$/u")
	private String firstName;

	private String lastName;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL, mappedBy="personalDetails")
	private Customer customer;
	
//	@Past
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private SimpleDate birthday;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
}
