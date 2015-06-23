package com.nutrinfomics.geneway.server.requestfactory.request;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.PersonalDetails;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class EntityBaseService {

	@Inject Provider<EntityManager> entityManager;
	
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
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		sessionDb.getCustomer().setPersonalDetails(personalDetails);
		personalDetails.setCustomer(sessionDb.getCustomer());
	}

}
