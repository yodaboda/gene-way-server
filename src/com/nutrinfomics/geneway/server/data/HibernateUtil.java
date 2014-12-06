package com.nutrinfomics.geneway.server.data;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;

import org.hibernate.jpa.HibernatePersistenceProvider;

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
		}
		
	}
	
	public EntityManager getEntityManager(){
		return entityManagerFactory.createEntityManager();
	}
}
