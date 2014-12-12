package com.nutrinfomics.geneway.server.domain.customer;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mindrot.jbcrypt.BCrypt;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.data.UserMapperServices;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.contact.ContactInformation;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.PersonalizedLifeStyle;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.status.Status;
import com.nutrinfomics.geneway.server.domain.subscription.Subscription;

@Entity
@Table(indexes = { @Index(columnList = "username") })
public class Customer extends EntityBase{

	@NotNull
	@Size(min = 3, max = 30)
	private String username;
	
    private String hashedPassword;
	
	@Transient
	private String password;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private PersonalDetails personalDetails;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Subscription subscription;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Session session;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Device device;
	
	@Fetch(FetchMode.SELECT) // hibernate specific: need to add it to avoid "cannot simultaneously fetch multiple bags"
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private ContactInformation contactInformation;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Status status;
	
	private PersonalizedLifeStyle lifeStyle;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Plan plan;
	
	public Customer(){
	}

	public Plan getPlan(){
		return plan;
	}
	
	public void setPlan(Plan plan){
		this.plan = plan;
	}

	public ContactInformation getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(ContactInformation contactInformation) {
		this.contactInformation = contactInformation;
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
		hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
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

	public String getHashedPassword() {
		return hashedPassword;
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
