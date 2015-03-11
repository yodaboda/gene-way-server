package com.nutrinfomics.geneway.server.requestfactory.request;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import com.nutrinfomics.geneway.server.alert.message.SMSEmailMessage;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;

import com.nutrinfomics.geneway.shared.AuthenticationException;
import com.nutrinfomics.geneway.shared.AuthenticationException.AuthenticationExceptionType;

public class AuthenticationService {
	
	@Inject Provider<EntityManager> entityManager;
	
	@Transactional
	public Session register(Session session){
		SecureRandom random = new SecureRandom();

		String code = new BigInteger(130, random).toString(32).substring(0, 6);
		
		session.getCustomer().getDevice().setCode(code);
		session.getCustomer().getDevice().setCodeCreation(new Date());
		
		//TODO - if phone number associated with another device - check if it is inactive, then delete entry. Otherwise, return an exception of used phone-number
		//this is bad - could be a privacy breach. Need to think of workaround.
		
		entityManager.get().merge(session);
		
		//the phone number might not be attached to the session - it could be already in the DB
		Device deviceDb = new HibernateUtil().selectDeviceByUUID(session.getCustomer().getDevice().getUuid(), entityManager);
		
		SMSEmailMessage smsCode = new SMSEmailMessage(deviceDb.getPhonenumber(), code);
		try {
			smsCode.generateAndSendEmail();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//do not return anything from Db at this stage. Only after logging in we do so.
		session.getCustomer().getDevice().setCode(null);// do not send code by mistake to client
		
		return session;
		
	}
	
	@Transactional
	public Session authenticateCode(Session session) throws AuthenticationException{
		Device deviceDb = new HibernateUtil().selectDevice(session.getCustomer().getDevice().getPhonenumber(), entityManager);
		LocalDateTime creationTime = LocalDateTime.from(deviceDb.getCodeCreation().toInstant());
		LocalDateTime expiry = creationTime.plusMinutes(20);
		if(expiry.isBefore(LocalDateTime.now())){
			throw new AuthenticationException(AuthenticationExceptionType.EXPIRED);
		}
		if(deviceDb.getCode().equals(session.getCustomer().getDevice().getCode())){
			deviceDb.setCode(null);
			deviceDb.setCodeCreation(null);
			entityManager.get().merge(deviceDb);
			
			deviceDb.getCustomer().getCredentials().setHashedPassword(null); // do not send this to client
			deviceDb.getCustomer().getCredentials().setPassword(null); // do not send this to client
			//TODO: better have passwords in separate entity to avoid sending them by mistake
			// dont return anything from DB. only after logging in we do so.
			return session;
		}
		return null;
	}
	
	@Transactional
	public Session authenticateCustomer(Customer customer) throws AuthenticationException{
		Customer customerDb;

		try{
			customerDb = new HibernateUtil().selectCustomer(customer.getCredentials().getUsername(), entityManager);
		}
		catch(Exception e){
			throw new AuthenticationException(AuthenticationExceptionType.INVALID_USERNAME);
		}

		Session session = customerDb.getSession();

		if(session == null){
			session = new Session();
			customerDb.setSession(session);
			session.setCustomer(customerDb);
		}
		session.setSid(UUID.randomUUID().toString());

		entityManager.get().persist(session);

		boolean valid = customerDb.getCredentials().checkPassword(customer.getCredentials().getPassword());

		if(valid){
			Device device = customerDb.getDevice();

			if(device == null){
				device = new Device();
				device.setUuid(customer.getDevice().getUuid());
				customerDb.setDevice(device);
				device.setCustomer(customerDb);

				entityManager.get().persist(device);
			}

			if(!device.getUuid().equalsIgnoreCase(customer.getDevice().getUuid()) ||
					device.getCode() != null || device.getCodeCreation() != null){
				AuthenticationException loginException = new AuthenticationException(AuthenticationExceptionType.UNAUTHORIZED_DEVICE);
				throw loginException;
			}


			return customerDb.getSession();
		}
		else{
			throw new AuthenticationException(AuthenticationExceptionType.INVALID_PASSWORD);
		}
	}
	
	public Session authenticateSession(Session session) throws AuthenticationException{

		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);

		Customer customerDb = sessionDb.getCustomer();
		Device deviceDb = customerDb.getDevice();

		if( ! deviceDb.getUuid().equalsIgnoreCase(session.getCustomer().getDevice().getUuid()) ||
				! sessionDb.getSid().equalsIgnoreCase(session.getSid()) ){
			throw new AuthenticationException(AuthenticationExceptionType.INVALID_SESSION);
		}

		return customerDb.getSession();
	}

}