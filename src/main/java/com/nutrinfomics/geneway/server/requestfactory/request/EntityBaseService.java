package com.nutrinfomics.geneway.server.requestfactory.request;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.PersonalDetails;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class EntityBaseService {

	private Provider<EntityManager> entityManager;
	private HibernateUtil hibernateUtil;
	
	@Inject
	public EntityBaseService(Provider<EntityManager> entityManager, 
							HibernateUtil hibernateUtil) {
		this.entityManager = entityManager;
		this.hibernateUtil = hibernateUtil;
	}
	
	@Transactional
	public void persist(EntityBase entityBase){
		entityManager.get().persist(entityBase);
	}
	@Transactional
	public void merge(EntityBase entityBase){
		entityManager.get().merge(entityBase);
	}

	@Transactional
	public void remove(EntityBase entityBase){
		entityManager.get().remove(entityBase);
	}
	@Transactional
	public void mergePersonalDetails(Session session, PersonalDetails personalDetails){
		Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager);
		sessionDb.getCustomer().setPersonalDetails(personalDetails);
		personalDetails.setCustomer(sessionDb.getCustomer());
	}

}
