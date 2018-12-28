package com.nutrinfomics.geneway.server.data;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
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

  public Customer getCustomer(String username) {
    return selectCustomer(username);
  }

//  @Transactional
//  public Device selectDevice(String phoneNumber) {
//    TypedQuery<Device> query =
//        entityManagerProvider
//            .get()
//            .createQuery("SELECT d FROM Device d WHERE d.phonenumber = :phonenumber", Device.class)
//            .setParameter("phonenumber", phoneNumber);
//    return query.getSingleResult();
//  }

  @Transactional
  public Device selectDeviceByUUID(String uuid) {
    TypedQuery<Device> query =
        entityManager.createQuery("SELECT d FROM Device d WHERE d.uuid = :uuid", Device.class)
            .setParameter("uuid", uuid);
    return query.getSingleResult();
  }

  @Transactional
  public Customer selectCustomer(String username) {
    TypedQuery<Customer> query =
        entityManager
            .createQuery(
                "SELECT c FROM Customer c WHERE c.credentials.username = :username", Customer.class)
            .setParameter("username", username);
    return query.getSingleResult();
  }

  @Transactional
  public Customer selectCustomerBasedOnPhoneNumber(
      String registeredPhoneNumber) {
    TypedQuery<Customer> query =
        entityManager
            .createQuery(
                "SELECT c FROM Customer c WHERE c.contactInformation.phonenumber = :phonenumber",
                Customer.class)
            .setParameter("phonenumber", registeredPhoneNumber);
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

  @Transactional
  public List<Customer> getCustomers() {
    TypedQuery<Customer> query =
        entityManager.createQuery("SELECT c FROM Customer", Customer.class);
    return query.getResultList();
  }
}
