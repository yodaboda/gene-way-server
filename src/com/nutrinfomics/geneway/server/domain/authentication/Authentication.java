package com.nutrinfomics.geneway.server.domain.authentication;

import java.util.UUID;

import javax.persistence.EntityManager;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.authentication.AuthenticationException.LoginExceptionType;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class Authentication {
	public static Session authenticateCustomer(Customer customer) throws AuthenticationException{

		EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();
		try{
			entityManager.getTransaction().begin();
			Customer customerDb;

			try{
				customerDb = HibernateUtil.getInstance().getCustomer(customer.getUsername(), entityManager);
			}
			catch(Exception e){
				throw new AuthenticationException(LoginExceptionType.INVALID_USERNAME);
			}

			Session session = customerDb.getSession();

			if(session == null){
				session = new Session();
				entityManager.persist(session);
			}
			session.setSid(UUID.randomUUID().toString());

			customerDb.setSession(session);
			session.setCustomer(customerDb);

			boolean valid = customerDb.checkPassword(customer.getPassword());

			if(valid){
				Device device = customerDb.getDevice();

				if(device == null){
					device = new Device();
					entityManager.persist(device);

					device.setUuid(customer.getDevice().getUuid());
					customerDb.setDevice(device);
					device.setCustomer(customerDb);
				}

				if(!device.getUuid().equalsIgnoreCase(customer.getDevice().getUuid())){
					AuthenticationException loginException = new AuthenticationException(LoginExceptionType.UNAUTHORIZED_DEVICE);
					throw loginException;
				}

				customer.getSession().setSid(customerDb.getSession().getSid());
				
				entityManager.getTransaction().commit();
				
				return customer.getSession();
			}
			else{
				throw new AuthenticationException(LoginExceptionType.INVALID_PASSWORD);
			}
		}
		finally{
			entityManager.close();
		}
	}
	
	public static Session authenticateSession(Session session) throws AuthenticationException{

		Session sessionDb = HibernateUtil.getInstance().getSession(session.getSid());

		Customer customerDb = sessionDb.getCustomer();
		Device deviceDb = customerDb.getDevice();

		if( ! deviceDb.getUuid().equalsIgnoreCase(session.getCustomer().getDevice().getUuid()) ||
				! sessionDb.getSid().equalsIgnoreCase(session.getSid()) ){
			throw new AuthenticationException(LoginExceptionType.INVALID_SESSION);
		}

		return customerDb.getSession();
	}

	public static Customer registerCustomer(Customer customer){
		customer.persist();
		return customer;
	}
}