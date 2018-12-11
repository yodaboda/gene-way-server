package com.nutrinfomics.geneway.server.requestfactory.request;

import static org.junit.Assert.*;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.junit.Test;
import org.mockito.Mock;

import com.nutrinfomics.geneway.server.domain.device.Session;

public class CommunityServiceTest {

	@Mock
	private Provider<EntityManager> mockEntityManagerProvider;
	@Mock
	private Session mockSession;
	
	
	private CommunityService communityService = new CommunityService(mockEntityManagerProvider);
	
	@Test
	public void testCommunityUpdates() {
		communityService.communityUpdates(mockSession);
	}

}
