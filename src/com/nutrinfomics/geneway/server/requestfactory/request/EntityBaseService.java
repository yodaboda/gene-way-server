package com.nutrinfomics.geneway.server.requestfactory.request;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.domain.EntityBase;

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

}
