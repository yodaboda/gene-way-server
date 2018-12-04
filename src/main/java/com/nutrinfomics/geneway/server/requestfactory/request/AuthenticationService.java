package com.nutrinfomics.geneway.server.requestfactory.request;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;
import com.nutrinfomics.geneway.shared.AuthenticationException;
import com.nutrinfomics.geneway.shared.AuthenticationException.AuthenticationExceptionType;

public class AuthenticationService {
	
	private Provider<EntityManager> entityManager;
	private HibernateUtil hibernateUtil;
	
	@Inject
	public AuthenticationService(Provider<EntityManager> entityManager, 
								HibernateUtil hibernateUtil) {
		this.entityManager = entityManager;
		this.hibernateUtil = hibernateUtil;
	}

	@Transactional
	public boolean unlock(Identifier identifier){
		try{
			Identifier dbIdentifier = hibernateUtil.selectIdentifier(identifier.getIdentifierCode(), entityManager);
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
	public String confirmValuationTermsOfService(String uuid){
		Identifier dbIdentifier = hibernateUtil.selectIdentifierFromUUID(uuid, entityManager);
		String ip = new Utils().getIP(new RequestUtils());
		dbIdentifier.setEvaluationTermsAcceptanceIP(ip);
		dbIdentifier.setEvaluationTermsAcceptanceTime(new Date());
		return ip;
	}

	@Transactional
	public boolean authenticateCode(Customer customer) throws AuthenticationException{
		Device deviceDb = hibernateUtil.selectDeviceByUUID(customer.getDevice().getUuid(), entityManager);
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
			deviceDb = hibernateUtil.selectDeviceByUUID(customer.getDevice().getUuid(), entityManager);
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

		Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager);

		Customer customerDb = sessionDb.getCustomer();
		Device deviceDb = customerDb.getDevice();

		if( ! deviceDb.getUuid().equalsIgnoreCase(session.getCustomer().getDevice().getUuid()) ||
				! sessionDb.getSid().equalsIgnoreCase(session.getSid()) ){
			throw new AuthenticationException(AuthenticationExceptionType.INVALID_SESSION);
		}

		return customerDb.getSession();
	}

}