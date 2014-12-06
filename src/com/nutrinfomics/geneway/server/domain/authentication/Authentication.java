package com.nutrinfomics.geneway.server.domain.authentication;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.authentication.AuthenticationException.LoginExceptionType;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class Authentication {
	public static Customer authenticateCustomer(String userName, String password, String uuid) throws AuthenticationException{
		
		EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();
		TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c WHERE c.username = :username", Customer.class).setParameter("username", userName);
		
		Customer customer;
		try{
			customer = query.getSingleResult();
		}
		catch(Exception e){
			throw new AuthenticationException(LoginExceptionType.INVALID_USERNAME);
		}

		Session session = new Session();
		UUID sessionID = UUID.randomUUID();
		session.setSid(sessionID.toString());
		session.setCustomer(customer);
		
		customer.setSession(session);
		
		boolean hasHashedPassword = customer.hasHashedPassword();
		
		if(!hasHashedPassword){ // first-time login
			Device device = new Device();
			device.setUuid(uuid);
			device.setCustomer(customer);

			customer.setDevice(device);

			entityManager.getTransaction().begin();
			entityManager.persist(device);
			entityManager.persist(session);
			entityManager.getTransaction().commit();
		}

		boolean valid = customer.checkPassword(password);

		if(valid){
			Device device = customer.getDevice();

			if(device == null || !device.getUuid().equalsIgnoreCase(uuid)){
				AuthenticationException loginException = new AuthenticationException(LoginExceptionType.UNAUTHORIZED_DEVICE);
				throw loginException;
			}
			
			return customer;
		}
		else{
			throw new AuthenticationException(LoginExceptionType.INVALID_PASSWORD);
		}
	}
	
	public static Customer authenticateSession(String sid, String uuid) throws AuthenticationException{

		EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();
		TypedQuery<Session> query = entityManager.createQuery("SELECT s FROM Session s WERE s.sid = :sid", Session.class).setParameter("sid", sid);
		Session session = query.getSingleResult();

		Customer customer = session.getCustomer();
		Device device = customer.getDevice();

		if( ! device.getUuid().equalsIgnoreCase(uuid) ||
				! session.getSid().equalsIgnoreCase(sid) ){
			throw new AuthenticationException(LoginExceptionType.INVALID_SESSION);
		}

		return customer;
	}

	public static Customer registerCustomer(String username, String password, String uuid){
		return null;
	}
}
