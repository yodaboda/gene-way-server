package com.nutrinfomics.geneway.server.domain;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Locator;

public class EntityBaseLocator extends Locator<EntityBase, Long>{

	@Inject
	Provider<EntityManager> entityManager;

	@Override
	public EntityBase create(Class<? extends EntityBase> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public EntityBase find(Class<? extends EntityBase> clazz, Long id) {
		return entityManager.get().find(clazz, id); 
	}

	@Override
	public Class<EntityBase> getDomainType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long getId(EntityBase domainObject) {
		return domainObject.getId();
	}

	@Override
	public Class<Long> getIdType() {
		return Long.class;
	}

	@Override
	public Object getVersion(EntityBase domainObject) {
		return domainObject.getVersion();
	}

}
