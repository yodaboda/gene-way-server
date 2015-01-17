package com.nutrinfomics.geneway.server.requestfactory.request;

import java.util.UUID;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.requestfactory.request.AuthenticationException.LoginExceptionType;

public class AuthenticationService {
	
	@Inject Provider<EntityManager> entityManager;

	
	@Transactional
	public Session authenticateCustomer(Customer customer) throws AuthenticationException{
		Customer customerDb;

		try{
			customerDb = new HibernateUtil().selectCustomer(customer.getUsername(), entityManager);
		}
		catch(Exception e){
			throw new AuthenticationException(LoginExceptionType.INVALID_USERNAME);
		}

		Session session = customerDb.getSession();

		if(session == null){
			session = new Session();
			customerDb.setSession(session);
			session.setCustomer(customerDb);
		}
		session.setSid(UUID.randomUUID().toString());

		entityManager.get().persist(session);

		boolean valid = customerDb.checkPassword(customer.getPassword());

		if(valid){
			Device device = customerDb.getDevice();

			if(device == null){
				device = new Device();
				device.setUuid(customer.getDevice().getUuid());
				customerDb.setDevice(device);
				device.setCustomer(customerDb);

				entityManager.get().persist(device);
			}

			if(!device.getUuid().equalsIgnoreCase(customer.getDevice().getUuid())){
				AuthenticationException loginException = new AuthenticationException(LoginExceptionType.UNAUTHORIZED_DEVICE);
				throw loginException;
			}


			return customerDb.getSession();
		}
		else{
			throw new AuthenticationException(LoginExceptionType.INVALID_PASSWORD);
		}
	}
	
	public Session authenticateSession(Session session) throws AuthenticationException{

		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);

		Customer customerDb = sessionDb.getCustomer();
		Device deviceDb = customerDb.getDevice();

		if( ! deviceDb.getUuid().equalsIgnoreCase(session.getCustomer().getDevice().getUuid()) ||
				! sessionDb.getSid().equalsIgnoreCase(session.getSid()) ){
			throw new AuthenticationException(LoginExceptionType.INVALID_SESSION);
		}

		return customerDb.getSession();
	}

}