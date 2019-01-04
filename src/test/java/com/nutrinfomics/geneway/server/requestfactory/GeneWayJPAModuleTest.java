package com.nutrinfomics.geneway.server.requestfactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistService;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class GeneWayJPAModuleTest {

  private final String SID = "no taxes without representation";

  @Mock private HibernateUtil mockHibernateUtil;

  @Mock private Session mockDbSession;
  @Bind @Mock private Session mockClientSession;

  @Inject private PersistService service;
  @Inject Provider<EntityManager> entityManagerProvider;

  private Injector injector;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    injector =
        Guice.createInjector(
            Modules.override(new GeneWayJPAModule())
                .with(new TestGeneWayJPAModule(mockHibernateUtil)),
            BoundFieldModule.of(this));
    injector.injectMembers(this);
    service.start();
  }

  @Test
  public void provideDbSession_AsExpected() {
    doReturn(SID).when(mockClientSession).getSid();
    when(mockHibernateUtil.selectSession(SID)).thenReturn(mockDbSession);

    Session dbSession = injector.getInstance(Key.get(Session.class, Names.named("dbSession")));
    assertEquals(mockDbSession, dbSession);
  }
}
