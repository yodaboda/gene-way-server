package com.nutrinfomics.geneway.server.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;

public class HibernateUtil {

//	private EntityManagerFactory entityManagerFactory;

//	private static HibernateUtil instance;
	
	private Provider<EntityManager> entityManagerProvider;
	
//	public static HibernateUtil getInstance(){
//		if(instance == null){
//			instance = new HibernateUtil();
//		}
//		return instance;
//	}
	@Inject
	public HibernateUtil(Provider<EntityManager> entityManagerProvider){
		this.entityManagerProvider = entityManagerProvider;
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
		return selectCustomer(username, entityManagerProvider);
	}

	@Transactional
	public Device selectDevice(String phoneNumber, Provider<EntityManager> entityManager){
		TypedQuery<Device> query = entityManager.get().createQuery("SELECT d FROM Device d WHERE d.phonenumber = :phonenumber", Device.class).setParameter("phonenumber", phoneNumber);
		return query.getSingleResult();
	}

	@Transactional
	public Device selectDeviceByUUID(String uuid, Provider<EntityManager> entityManager){
		TypedQuery<Device> query = entityManager.get().createQuery("SELECT d FROM Device d WHERE d.uuid = :uuid", Device.class).setParameter("uuid", uuid);
		return query.getSingleResult();
	}

	@Transactional
	public Customer selectCustomer(String username, Provider<EntityManager> entityManager){
		TypedQuery<Customer> query = entityManager.get().createQuery("SELECT c FROM Customer c WHERE c.credentials.username = :username", Customer.class).setParameter("username", username);
		return query.getSingleResult();
	}

	@Transactional
	public Customer selectCustomerBasedOnPhoneNumber(
			String registeredPhoneNumber, Provider<EntityManager> entityManager) {
		TypedQuery<Customer> query = entityManager.get().createQuery("SELECT c FROM Customer c WHERE c.contactInformation.phonenumber = :phonenumber", Customer.class).setParameter("phonenumber", registeredPhoneNumber);
		return query.getSingleResult();
	}

	
	public Session getSession(String sid) {
		return selectSession(sid, entityManagerProvider);
	}
	
	@Transactional
	public Session selectSession(String sid, Provider<EntityManager> entityManager){
		TypedQuery<Session> query = entityManager.get().createQuery("SELECT s FROM Session s WHERE s.sid = :sid", Session.class).setParameter("sid", sid);
		return query.getSingleResult();
	}

	public Session selectSession(String sid) {
		return selectSession(sid, entityManagerProvider);
	}

	@Transactional
	public Identifier selectIdentifier(String identifierCode,
			Provider<EntityManager> entityManager) {
		TypedQuery<Identifier> query = entityManager.get().createQuery("SELECT iden FROM Identifier iden WHERE iden.identifierCode = :identifierCode", Identifier.class).setParameter("identifierCode", identifierCode);
		return query.getSingleResult();
	}

	@Transactional
	public Identifier selectIdentifierFromUUID(String uuid,
			Provider<EntityManager> entityManager) {
		TypedQuery<Identifier> query = entityManager.get().createQuery("SELECT iden FROM Identifier iden WHERE iden.uuid = :uuid", Identifier.class).setParameter("uuid", uuid);
		return query.getSingleResult();
	}

	@Transactional
	public List<Customer> getCustomers(Provider<EntityManager> entityManager) {
		TypedQuery<Customer> query = entityManager.get().createQuery("SELECT c FROM Customer", Customer.class);
		return query.getResultList();

	}
}
