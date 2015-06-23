package com.nutrinfomics.geneway.server.data;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;

public class HibernateUtil {

//	private EntityManagerFactory entityManagerFactory;

//	private static HibernateUtil instance;
	
	@Inject Provider<EntityManager> em;
	
//	public static HibernateUtil getInstance(){
//		if(instance == null){
//			instance = new HibernateUtil();
//		}
//		return instance;
//	}
	
	public HibernateUtil(){
//		try{
//			PersistenceProviderResolverHolder.setPersistenceProviderResolver(new PersistenceProviderResolver() {
//				private final List<PersistenceProvider> providers_ = Arrays.asList((PersistenceProvider) new HibernatePersistenceProvider());
//
//				@Override
//				public List<PersistenceProvider> getPersistenceProviders()
//				{ return providers_; }
//
//				@Override
//				public void clearCachedProviders() {
//
//				}
//			});
//			entityManagerFactory = Persistence.createEntityManagerFactory( "domainPersistence" );
//		}
//		catch(Exception _Ex){
//		    System.out.println("Erro: " + _Ex.getMessage());
//		    _Ex.printStackTrace();
//		}
	}
	
	public void shutdown(){
//		if(entityManagerFactory != null) entityManagerFactory.close();
	}
	
	public Customer getCustomer(String username){
		Customer customer = selectCustomer(username, em);
		return customer;
	}

	public Device selectDevice(String phoneNumber, Provider<EntityManager> entityManager){
		TypedQuery<Device> query = entityManager.get().createQuery("SELECT d FROM Device d WHERE d.phonenumber = :phonenumber", Device.class).setParameter("phonenumber", phoneNumber);
		return query.getSingleResult();
	}

	public Device selectDeviceByUUID(String uuid, Provider<EntityManager> entityManager){
		TypedQuery<Device> query = entityManager.get().createQuery("SELECT d FROM Device d WHERE d.uuid = :uuid", Device.class).setParameter("uuid", uuid);
		return query.getSingleResult();
	}

	
	public Customer selectCustomer(String username, Provider<EntityManager> entityManager){
		TypedQuery<Customer> query = entityManager.get().createQuery("SELECT c FROM Customer c WHERE c.credentials.username = :username", Customer.class).setParameter("username", username);
		return query.getSingleResult();
	}

	public Customer selectCustomerBasedOnPhoneNumber(
			String registeredPhoneNumber, Provider<EntityManager> entityManager) {
		TypedQuery<Customer> query = entityManager.get().createQuery("SELECT c FROM Customer c WHERE c.contactInformation.phonenumber = :phonenumber", Customer.class).setParameter("phonenumber", registeredPhoneNumber);
		return query.getSingleResult();
	}

	
	public Session getSession(String sid) {
		return selectSession(sid, em);
	}
	
	public Session selectSession(String sid, Provider<EntityManager> entityManager){
		TypedQuery<Session> query = entityManager.get().createQuery("SELECT s FROM Session s WHERE s.sid = :sid", Session.class).setParameter("sid", sid);
		Session sessionDb = query.getSingleResult();
		return sessionDb;
	}

	public Session selectSession(String sid) {
		return selectSession(sid, em);
	}

	public Identifier selectIdentifier(String identifierCode,
			Provider<EntityManager> entityManager) {
		TypedQuery<Identifier> query = entityManager.get().createQuery("SELECT iden FROM Identifier iden WHERE iden.identifierCode = :identifierCode", Identifier.class).setParameter("identifierCode", identifierCode);
		Identifier identifierDb = query.getSingleResult();
		return identifierDb;
	}
}
