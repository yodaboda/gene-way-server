package com.nutrinfomics.geneway.server.domain.customer;

import com.nutrinfomics.geneway.server.data.UserMapperServices;
import com.nutrinfomics.geneway.server.domain.ModelObject;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.PersonalizedLifeStyle;
import com.nutrinfomics.geneway.server.domain.status.Status;
import com.nutrinfomics.geneway.server.domain.subscription.Subscription;

public class Customer extends ModelObject{

	private String username;
	private String password;
	private PersonalDetails personalDetails;
	private Subscription subscription;
	private Session session;
	private Device device;
	private Status status;
	private PersonalizedLifeStyle lifeStyle;
	
	public Customer(){
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}
	
		
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public static Customer findCustomer(long id){
		Customer customer = UserMapperServices.getInstance().findCustomer(id);
		Session session = UserMapperServices.getInstance().getSession(customer);
		Device device = UserMapperServices.getInstance().getCustomerDevice(customer);
		
		customer.setDevice(device);
		customer.setSession(session);
		
		session.setCustomer(customer);
		device.setCustomer(customer);
		
		return customer;
	}
}
