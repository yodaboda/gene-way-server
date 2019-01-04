package com.nutrinfomics.geneway.server.requestfactory.request;

import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;

import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class CommunityServiceTest {

  @Mock private Provider<EntityManager> mockEntityManagerProvider;
  @Mock private Session mockSession;

  private CommunityService communityService = new CommunityService(mockEntityManagerProvider);

  @Test
  public void testCommunityUpdates() {
    communityService.communityUpdates(mockSession);
  }
}
