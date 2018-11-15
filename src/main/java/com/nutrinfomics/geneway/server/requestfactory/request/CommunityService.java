package com.nutrinfomics.geneway.server.requestfactory.request;

import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nutrinfomics.geneway.server.domain.community.CommunityUpdate;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class CommunityService {
	@Inject Provider<EntityManager> entityManager;
	
	public List<CommunityUpdate> communityUpdates(Session session){
		return null;
	}
}
