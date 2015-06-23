package com.nutrinfomics.geneway.server.requestfactory.request;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.alert.message.SMSEmailMessage;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;
import com.nutrinfomics.geneway.shared.AuthenticationException;
import com.nutrinfomics.geneway.shared.AuthenticationException.AuthenticationExceptionType;

public class AuthenticationService {
	
	@Inject Provider<EntityManager> entityManager;
	
	@Transactional
	public boolean unlock(Identifier identifier){
		try{
			Identifier dbIdentifier = new HibernateUtil().selectIdentifier(identifier.getIdentifierCode(), entityManager);
			if(dbIdentifier.getUuid() == null || dbIdentifier.getUuid().isEmpty()){
				dbIdentifier.setUuid(identifier.getUuid());
				return true;
			}
			return dbIdentifier.getUuid().equalsIgnoreCase(identifier.getUuid());
		}
		catch(NoResultException ex){
			return false;
		}
	}
	
	@Transactional
	public void register(Customer customer){
		SecureRandom random = new SecureRandom();

		String code = new BigInteger(130, random).toString(32).substring(0, 6);
		
		customer.getDevice().setCode(code);
		customer.getDevice().setCodeCreation(LocalDateTime.now());

		try{
			Customer customerDb = new HibernateUtil().selectCustomerBasedOnPhoneNumber(customer.getContactInformation().getRegisteredPhoneNumber(), entityManager);
			entityManager.get().remove(customerDb.getContactInformation());
			entityManager.get().remove(customerDb.getDevice());
			entityManager.get().remove(customerDb); // delete this device
		}
		catch(NoResultException ex){
			//all good - device not in db
		}

		//TODO - if phone number associated with another device - check if it is inactive, then delete entry. Otherwise, return an exception of used phone-number
		//this is bad - could be a privacy breach. Need to think of workaround.
		
		String hashedPassword = customer.getCredentials().hashPassword();
		
		customer.getCredentials().setHashedPassword(hashedPassword);
		
		entityManager.get().merge(customer);
		
		//the phone number might not be attached to the session - it could be already in the DB
		Device deviceDb = new HibernateUtil().selectDeviceByUUID(customer.getDevice().getUuid(), entityManager);
		
		SMSEmailMessage smsCode = new SMSEmailMessage(deviceDb.getCustomer().getContactInformation().getRegisteredPhoneNumber(), deviceDb.getCustomer().getNickName(), code);
		try {
			smsCode.generateAndSendEmail();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			// TODO return error
			e.printStackTrace();
		}
		//do not return anything from Db at this stage. Only after logging in we do so.
		customer.getDevice().setCode(null);// do not send code by mistake to client
		
	}
	
	@Transactional
	public boolean authenticateCode(Customer customer) throws AuthenticationException{
		Device deviceDb = new HibernateUtil().selectDeviceByUUID(customer.getDevice().getUuid(), entityManager);
		LocalDateTime creationTime = deviceDb.getCodeCreation();
		LocalDateTime expiry = creationTime.plusMinutes(20);
		if(expiry.isBefore(LocalDateTime.now())){
			throw new AuthenticationException(AuthenticationExceptionType.EXPIRED);
		}
		if(deviceDb.getCode().equals(customer.getDevice().getCode())){
			deviceDb.setCode(null);
			deviceDb.setCodeCreation(null);
			entityManager.get().merge(deviceDb);
			
			//do this *after* merging!!!
//			deviceDb.getCustomer().getCredentials().setHashedPassword(null); // do not send this to client
//			deviceDb.getCustomer().getCredentials().setPassword(null); // do not send this to client
			//TODO: better have passwords in separate entity to avoid sending them by mistake
			// dont return anything from DB. only after logging in we do so.
			return true;
		}
		return false;
	}
	
	@Transactional
	public Session authenticateCustomer(Customer customer) throws AuthenticationException{
		Customer customerDb;
		Device deviceDb;

		try{
			deviceDb = new HibernateUtil().selectDeviceByUUID(customer.getDevice().getUuid(), entityManager);
			customerDb = deviceDb.getCustomer();
		}
		catch(Exception e){
			throw new AuthenticationException(AuthenticationExceptionType.INVALID_DEVICE_UUID);
		}

		Session session = customerDb.getSession();

		if(session == null){
			session = new Session();
			customerDb.setSession(session);
			session.setCustomer(customerDb);
		}
		session.setSid(UUID.randomUUID().toString());


		boolean valid = customerDb.getCredentials().checkPassword(customer.getCredentials().getPassword());

		entityManager.get().persist(session);

		
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
	
	@Transactional
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