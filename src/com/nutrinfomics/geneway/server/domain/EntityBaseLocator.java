package com.nutrinfomics.geneway.server.domain;

import javax.persistence.EntityManager;

import com.google.web.bindery.requestfactory.shared.ExtraTypes;
import com.google.web.bindery.requestfactory.shared.Locator;
import com.nutrinfomics.geneway.server.data.HibernateUtil;

public class EntityBaseLocator extends Locator<EntityBase, Long>{

	private EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();

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
		return entityManager.find(clazz, id); 
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
