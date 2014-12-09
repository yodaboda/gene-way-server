package com.nutrinfomics.geneway.server.domain.customer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Persistence;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mindrot.jbcrypt.BCrypt;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.data.UserMapperServices;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.PersonalizedLifeStyle;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.status.Status;
import com.nutrinfomics.geneway.server.domain.subscription.Subscription;

@Entity
public class Customer extends EntityBase{

	@NotNull
	@Size(min = 3, max = 30)
	private String username;
	
    private String hashedPassword;
	
	@Transient
	private String password;
	
	private PersonalDetails personalDetails;
	private Subscription subscription;
	
//	@NotNull
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Session session;
	
//	@NotNull
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
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

//	public static Customer findCustomer(long id){
//		
//		try{
//			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory( "domainPersistence" );
//			entityManagerFactory.createEntityManager();
//		}
//		catch(Exception ex){
//			ex.printStackTrace();
////		    System.out.println("Erro: " + _Ex.getMessage());
//		}
//
////		Customer customer2 = HibernateUtil.getInstance().getEntityManager().find(Customer.class, 2);
//		
////		customer2.getId();
//		
//		Customer customer = UserMapperServices.getInstance().findCustomer(id);
//		Session session = UserMapperServices.getInstance().getSession(customer);
//		Device device = UserMapperServices.getInstance().getCustomerDevice(customer);
//		
//		customer.setDevice(device);
//		customer.setSession(session);
//		
//		session.setCustomer(customer);
//		device.setCustomer(customer);
//		
//		return customer;
//	}

	public String getHashedPassword() {
		if(hashedPassword == null){
			hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		}
		return hashedPassword;
	}

	public boolean hasPassword(){
		return password != null;
	}
	
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
	
	public boolean checkPassword(String plainTextPassword){
		if(password != null){
			return password.equals(plainTextPassword);
		}
		else{
			if(hashedPassword != null){
				return BCrypt.checkpw(plainTextPassword, hashedPassword);
			}
			else{
				return false;
			}
		}

	}
}
