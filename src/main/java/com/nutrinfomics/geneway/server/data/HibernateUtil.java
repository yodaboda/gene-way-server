package com.nutrinfomics.geneway.server.data;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;

@RequestScoped
public class HibernateUtil {
  private EntityManager entityManager;

  @Inject
  public HibernateUtil(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  // TODO: check if this method is needed.
  public void shutdown() {
    //		if(entityManagerFactory != null) entityManagerFactory.close();
  }

  @Transactional
  public Device selectDeviceByUUID(String uuid) {
    TypedQuery<Device> query =
        entityManager
            .createQuery("SELECT d FROM Device d WHERE d.uuid = :uuid", Device.class)
            .setParameter("uuid", uuid);
    return query.getSingleResult();
  }

  @Transactional
  public Session selectSession(String sid) {
    TypedQuery<Session> query =
        entityManager
            .createQuery("SELECT s FROM Session s WHERE s.sid = :sid", Session.class)
            .setParameter("sid", sid);
    return query.getSingleResult();
  }

  @Transactional
  public Identifier selectIdentifier(String identifierCode) {
    TypedQuery<Identifier> query =
        entityManager
            .createQuery(
                "SELECT iden FROM Identifier iden WHERE iden.identifierCode = :identifierCode",
                Identifier.class)
            .setParameter("identifierCode", identifierCode);
    return query.getSingleResult();
  }

  @Transactional
  public Identifier selectIdentifierFromUUID(String uuid) {
    TypedQuery<Identifier> query =
        entityManager
            .createQuery(
                "SELECT iden FROM Identifier iden WHERE iden.uuid = :uuid", Identifier.class)
            .setParameter("uuid", uuid);
    return query.getSingleResult();
  }
}
