package com.nutrinfomics.geneway.server.data;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;

public class HibernateUtilIntegrationTest {

  private PersistService service;

  private Injector injector;

  @Inject private HibernateUtil hibernateUtil;

  private EntityManager entityManager;
  private EntityTransaction entityTransaction;

  public class BindingModule extends AbstractModule {

    @Override
    protected void configure() {
      bindScope(RequestScoped.class, Scopes.SINGLETON);
    }
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    injector =
        Guice.createInjector(
            new JpaPersistModule("testUnit"), new BindingModule(), BoundFieldModule.of(this));

    service = injector.getInstance(PersistService.class);
    service.start();

    injector.injectMembers(this);

    entityManager = injector.getInstance(EntityManager.class);
    entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();
  }

  @After
  public void shotdown() {
    entityTransaction.rollback();
    entityManager.close();
    service.stop();
  }

  @Test
  public void selectDeviceByUUID_AsExpected() {
    String uuid = "777";
    String code = "e";
    Device device = new Device();
    device.setUuid(uuid);
    device.setCode(code);
    entityManager.persist(device);

    Device deviceDb = hibernateUtil.selectDeviceByUUID(uuid);
    assertEquals(uuid, deviceDb.getUuid());
    assertEquals(code, deviceDb.getCode());
    assertEquals(device.getId(), deviceDb.getId());
  }

  @Test
  public void selectSession_AsExpected() {
    Session session = new Session();
    String sid = "Provider";
    session.setSid(sid);

    entityManager.persist(session);

    Session sessionDb = hibernateUtil.selectSession(sid);
    assertEquals(session.getId(), sessionDb.getId());
    assertEquals(sid, sessionDb.getSid());
  }

  @Test
  public void selectIdentifier_AsExpected() {
    Identifier identifier = new Identifier();
    String identifierCode = "FNS2";
    identifier.setIdentifierCode(identifierCode);

    entityManager.persist(identifier);

    Identifier identifierDb = hibernateUtil.selectIdentifier(identifierCode);
    assertEquals(identifier.getId(), identifierDb.getId());
    assertEquals(identifierCode, identifierDb.getIdentifierCode());
  }

  @Test
  public void selectIdentifierFromUUID_AsExpected() {
    String UUID = "1234567d-e89b-12d3-a170-426655445000";
    String identifierCode = "LFAI";
    Identifier identifier = new Identifier();
    identifier.setUuid(UUID);
    identifier.setIdentifierCode(identifierCode);
    entityManager.persist(identifier);

    Identifier identifierDb = hibernateUtil.selectIdentifierFromUUID(UUID);
    assertEquals(identifier.getId(), identifierDb.getId());
    assertEquals(UUID, identifierDb.getUuid());
    assertEquals(identifierCode, identifierDb.getIdentifierCode());
  }
}
