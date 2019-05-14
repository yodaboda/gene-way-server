/*
 * Copyright 2019 Firas Swidan†
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nutrinfomics.geneway.server.requestfactory.request;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;
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

@RequestScoped
public class AuthenticationService {

  /** Logger for unexpected events. */
  private static final Logger LOGGER = LogManager.getLogger();

  private EntityManager entityManager;
  private HibernateUtil hibernateUtil;
  private Clock clock;
  private Utils utils;
  private UUIDGenerator uuidGenerator;
  private PasswordUtils passwordUtils;

  @Inject
  public AuthenticationService(
      EntityManager entityManager,
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
      Identifier dbIdentifier = hibernateUtil.selectIdentifier(identifier.getIdentifierCode());
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
    Identifier dbIdentifier = hibernateUtil.selectIdentifierFromUUID(uuid);
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
    Device deviceDb = hibernateUtil.selectDeviceByUUID(customer.getDevice().getUuid());
    OffsetDateTime creationTime = deviceDb.getCodeCreationTimestamp();
    OffsetDateTime expiry = creationTime.plusMinutes(20);
    if (expiry.isBefore(OffsetDateTime.now(clock))) {
      throw new AuthenticationException(AuthenticationExceptionType.EXPIRED);
    }
    if (deviceDb.getCode().equals(customer.getDevice().getCode())) {
      deviceDb.setCode(null);
      deviceDb.setCodeCreationTimestamp(null);
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
      deviceDb = hibernateUtil.selectDeviceByUUID(customer.getDevice().getUuid());
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
    if (credentialsDb.getPassword() != null) {
      valid = passwordUtils.checkPassword(clientPassword, credentialsDb.getPassword());
    } else {
      valid = passwordUtils.checkHashedPassword(clientPassword, credentialsDb.getHashedPassword());
    }

    entityManager.persist(session);

    if (valid) {
      Device device = customerDb.getDevice();

      if (device == null) {
        device = new Device();
        device.setUuid(customer.getDevice().getUuid());
        customerDb.setDevice(device);
        device.setCustomer(customerDb);

        entityManager.persist(device);
      }

      if (!device.getUuid().equalsIgnoreCase(customer.getDevice().getUuid())
          || device.getCode() != null
          || device.getCodeCreationTimestamp() != null) {
        throw new AuthenticationException(AuthenticationExceptionType.UNAUTHORIZED_DEVICE);
      }

      return customerDb.getSession();
    } else {
      throw new AuthenticationException(AuthenticationExceptionType.INVALID_PASSWORD);
    }
  }

  @Transactional
  public Session authenticateSession(Session session) throws AuthenticationException {

    Session sessionDb = hibernateUtil.selectSession(session.getSid());

    Customer customerDb = sessionDb.getCustomer();
    Device deviceDb = customerDb.getDevice();

    if (!deviceDb.getUuid().equalsIgnoreCase(session.getCustomer().getDevice().getUuid())) {
      throw new AuthenticationException(AuthenticationExceptionType.INVALID_SESSION);
    }

    return customerDb.getSession();
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */
