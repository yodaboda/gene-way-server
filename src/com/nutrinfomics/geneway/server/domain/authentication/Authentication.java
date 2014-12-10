package com.nutrinfomics.geneway.server.domain.authentication;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.authentication.AuthenticationException.LoginExceptionType;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class Authentication {
	public static Session authenticateCustomer(Customer customer) throws AuthenticationException{
		
		EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();
		Customer customerDb;
		
		try{
			customerDb = HibernateUtil.getInstance().getCustomer(customer.getUsername());
		}
		catch(Exception e){
			throw new AuthenticationException(LoginExceptionType.INVALID_USERNAME);
		}

		Session session = customerDb.getSession();
		
		entityManager.getTransaction().begin();
		session.setSid(UUID.randomUUID().toString());
		entityManager.getTransaction().commit();
		
		boolean valid = customerDb.checkPassword(customer.getPassword());

		if(valid){
			Device device = customerDb.getDevice();

			if(device == null || !device.getUuid().equalsIgnoreCase(customer.getDevice().getUuid())){
				AuthenticationException loginException = new AuthenticationException(LoginExceptionType.UNAUTHORIZED_DEVICE);
				throw loginException;
			}
			
			return customerDb.getSession();
		}
		else{
			throw new AuthenticationException(LoginExceptionType.INVALID_PASSWORD);
		}
	}
	
	public static Session authenticateSession(Session session) throws AuthenticationException{

		EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();
		TypedQuery<Session> query = entityManager.createQuery("SELECT s FROM Session s WERE s.sid = :sid", Session.class).setParameter("sid", session.getSid());
		Session sessionDb = query.getSingleResult();

		Customer customerDb = sessionDb.getCustomer();
		Device deviceDb = customerDb.getDevice();

		if( ! deviceDb.getUuid().equalsIgnoreCase(session.getCustomer().getDevice().getUuid()) ||
				! sessionDb.getSid().equalsIgnoreCase(session.getSid()) ){
			throw new AuthenticationException(LoginExceptionType.INVALID_SESSION);
		}

		return customerDb.getSession();
	}

	public static Customer registerCustomer(Customer customer){
		EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.persist(customer);
		entityManager.getTransaction().commit();
		
		return customer;
	}
}