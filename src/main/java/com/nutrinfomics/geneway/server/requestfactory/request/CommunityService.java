package com.nutrinfomics.geneway.server.requestfactory.request;

import java.util.List;

import javax.persistence.EntityManager;

import com.nutrinfomics.geneway.server.domain.community.CommunityUpdate;
import com.nutrinfomics.geneway.server.domain.device.Session;
import javax.inject.Inject;
import javax.inject.Provider;

public class CommunityService {
	
	private Provider<EntityManager> entityManager;
	
	@Inject
	public CommunityService(Provider<EntityManager> entityManager) {
		this.entityManager = entityManager;
	}
	
	public List<CommunityUpdate> communityUpdates(Session session){
		return null;
	}
}
