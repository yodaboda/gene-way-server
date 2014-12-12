package com.nutrinfomics.geneway.server.data;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;

import org.hibernate.jpa.HibernatePersistenceProvider;

import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class HibernateUtil {

	private EntityManagerFactory entityManagerFactory;

	private static HibernateUtil instance;
	
	public static HibernateUtil getInstance(){
		if(instance == null){
			instance = new HibernateUtil();
		}
		return instance;
	}
	
	public HibernateUtil(){
		try{
			PersistenceProviderResolverHolder.setPersistenceProviderResolver(new PersistenceProviderResolver() {
				private final List<PersistenceProvider> providers_ = Arrays.asList((PersistenceProvider) new HibernatePersistenceProvider());

				@Override
				public List<PersistenceProvider> getPersistenceProviders()
				{ return providers_; }

				@Override
				public void clearCachedProviders() {

				}
			});
			entityManagerFactory = Persistence.createEntityManagerFactory( "domainPersistence" );
		}
		catch(Exception _Ex){
		    System.out.println("Erro: " + _Ex.getMessage());
		    _Ex.printStackTrace();
		}
	}
	
	public void shutdown(){
		if(entityManagerFactory != null) entityManagerFactory.close();
	}
	
	public EntityManager getEntityManager(){
		return entityManagerFactory.createEntityManager();
	}
	
	public Customer getCustomer(String username){
		EntityManager entityManager = getEntityManager();
		TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c WHERE c.username = :username", Customer.class).setParameter("username", username);
		
		return query.getSingleResult();
	}

	public Session getSession(String sid) {
		EntityManager entityManager = getInstance().getEntityManager();
		TypedQuery<Session> query = entityManager.createQuery("SELECT s FROM Session s WHERE s.sid = :sid", Session.class).setParameter("sid", sid);
		Session sessionDb = query.getSingleResult();
		return sessionDb;
	}
}
