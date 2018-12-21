package com.nutrinfomics.geneway.server.requestfactory.request;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.PasswordUtils;
import com.nutrinfomics.geneway.server.UUIDGenerator;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Credentials;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;
import com.nutrinfomics.geneway.shared.AuthenticationException;
import com.nutrinfomics.geneway.shared.AuthenticationException.AuthenticationExceptionType;

public class AuthenticationService {

  /** Logger for unexpected events. */
  private static final Logger LOGGER = LogManager.getLogger();

  private Provider<EntityManager> entityManager;
  private HibernateUtil hibernateUtil;
  private Clock clock;
  private Utils utils;
  private UUIDGenerator uuidGenerator;
  private PasswordUtils passwordUtils;

  @Inject
  public AuthenticationService(
      Provider<EntityManager> entityManager,
      HibernateUtil hibernateUtil,
      Clock clock,
      Utils utils,
      UUIDGenerator uuidGenerator,
      PasswordUtils passwordUitls) {
    this.entityManager = entityManager;
    this.hibernateUtil = hibernateUtil;
    this.clock = clock;
    this.utils = utils;
    this.uuidGenerator = uuidGenerator;
    this.passwordUtils = passwordUitls;
  }

  @Transactional
  public boolean unlock(Identifier identifier) {
    try {
      Identifier dbIdentifier =
          hibernateUtil.selectIdentifier(identifier.getIdentifierCode(), entityManager);
      if (dbIdentifier.getUuid() == null || dbIdentifier.getUuid().isEmpty()) {
        dbIdentifier.setUuid(identifier.getUuid());
        return true;
      }
      return dbIdentifier.getUuid().equalsIgnoreCase(identifier.getUuid());
    } catch (NoResultException ex) {
      LOGGER.log(Level.DEBUG, ex.toString(), ex);
      return false;
    }
  }

  @Transactional
  public String confirmValuationTermsOfService(String uuid) {
    Identifier dbIdentifier = hibernateUtil.selectIdentifierFromUUID(uuid, entityManager);
    if (dbIdentifier == null) {
      String errorMessage = "UUID " + uuid + " is not on record";
      LOGGER.log(Level.WARN, errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
    String ip = utils.getIP();
    dbIdentifier.setEvaluationTermsAcceptanceIP(ip);
    dbIdentifier.setEvaluationTermsAcceptanceTime(Date.from(clock.instant()));
    return ip;
  }

  @Transactional
  public boolean authenticateCode(Customer customer) throws AuthenticationException {
    Device deviceDb =
        hibernateUtil.selectDeviceByUUID(customer.getDevice().getUuid(), entityManager);
    LocalDateTime creationTime = deviceDb.getCodeCreation();
    LocalDateTime expiry = creationTime.plusMinutes(20);
    if (expiry.isBefore(LocalDateTime.now(clock))) {
      throw new AuthenticationException(AuthenticationExceptionType.EXPIRED);
    }
    if (deviceDb.getCode().equals(customer.getDevice().getCode())) {
      deviceDb.setCode(null);
      deviceDb.setCodeCreation(null);
      // entityManager.get().merge(deviceDb);

      // do this *after* merging!!!
      //			deviceDb.getCustomer().getCredentials().setHashedPassword(null); // do not send this to
      // client
      //			deviceDb.getCustomer().getCredentials().setPassword(null); // do not send this to client
      // TODO: better have passwords in separate entity to avoid sending them by mistake
      // dont return anything from DB. only after logging in we do so.
      return true;
    }
    return false;
  }

  @Transactional
  public Session authenticateCustomer(Customer customer) throws AuthenticationException {
    Customer customerDb;
    Device deviceDb;

    try {
      deviceDb = hibernateUtil.selectDeviceByUUID(customer.getDevice().getUuid(), entityManager);
      customerDb = deviceDb.getCustomer();
    } catch (Exception e) {
      throw new AuthenticationException(AuthenticationExceptionType.INVALID_DEVICE_UUID);
    }

    Session session = customerDb.getSession();

    if (session == null) {
      session = new Session();
      customerDb.setSession(session);
      session.setCustomer(customerDb);
    }
    session.setSid(uuidGenerator.randomUUID().toString());

    Credentials credentialsDb = customerDb.getCredentials();
    String clientPassword = customer.getCredentials().getPassword();
    boolean valid;
    if(credentialsDb.getPassword() != null) {
    	valid = passwordUtils.checkPassword(clientPassword, credentialsDb.getPassword());
    }
    else {
    	valid = passwordUtils.checkHashedPassword(clientPassword, credentialsDb.getHashedPassword());
    }
   
        

    entityManager.get().persist(session);

    if (valid) {
      Device device = customerDb.getDevice();

      if (device == null) {
        device = new Device();
        device.setUuid(customer.getDevice().getUuid());
        customerDb.setDevice(device);
        device.setCustomer(customerDb);

        entityManager.get().persist(device);
      }

      if (!device.getUuid().equalsIgnoreCase(customer.getDevice().getUuid())
          || device.getCode() != null
          || device.getCodeCreation() != null) {
        throw new AuthenticationException(AuthenticationExceptionType.UNAUTHORIZED_DEVICE);
      }

      return customerDb.getSession();
    } else {
      throw new AuthenticationException(AuthenticationExceptionType.INVALID_PASSWORD);
    }
  }

  @Transactional
  public Session authenticateSession(Session session) throws AuthenticationException {

    Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager);

    Customer customerDb = sessionDb.getCustomer();
    Device deviceDb = customerDb.getDevice();

    if (!deviceDb.getUuid().equalsIgnoreCase(session.getCustomer().getDevice().getUuid())) {
      throw new AuthenticationException(AuthenticationExceptionType.INVALID_SESSION);
    }

    return customerDb.getSession();
  }
}
