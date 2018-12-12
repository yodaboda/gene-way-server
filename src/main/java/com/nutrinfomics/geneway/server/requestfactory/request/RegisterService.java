package com.nutrinfomics.geneway.server.requestfactory.request;

import java.time.Clock;
import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geneway.alerts.AlertMechanism;
import com.geneway.alerts.impl.EmailAlertMechanism;
import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;

public class RegisterService {
  /** Logger for unexpected events. */
  private static final Logger LOGGER = LogManager.getLogger();

  private Provider<EntityManager> entityManagerProvider;
  private AlertMechanism alertMechanism;
  private String code;
  private HibernateUtil hibernateUtil;
  private Clock clock;

  @Inject
  public RegisterService(
      Provider<EntityManager> entityManagerProvider,
      AlertMechanism alertMechanism,
      @Named("code") String code,
      HibernateUtil hibernateUtil,
      Clock clock) {
    this.entityManagerProvider = entityManagerProvider;
    this.alertMechanism = alertMechanism;
    this.code = code;
    this.hibernateUtil = hibernateUtil;
    this.clock = clock;
  }

  public void register(Customer customer) {
    if (customer == null) {
      throw new IllegalArgumentException("The customer cannot be null");
    }

    registerCustomer(customer);
    String phoneNumber = getCustomerPhoneNumber(customer);
    sendAlert(phoneNumber);
  }

  @Transactional
  String getCustomerPhoneNumber(Customer customer) {
    // the phone number might not be attached to the session - it could be already in the DB
    Device deviceDb =
        hibernateUtil.selectDeviceByUUID(customer.getDevice().getUuid(), entityManagerProvider);

    return deviceDb.getCustomer().getContactInformation().getRegisteredPhoneNumber();
  }

  void sendAlert(String phoneNumber) {
    //		SMSOverEmailAlertMechanism alertMechanism = new SMSOverEmailAlertMechanism(new
    // GeneWayCodeSMSAlertMessage( deviceDb.getCustomer().getNickName(), code),
    //		new GeneWaySMSAlertRecipient(),
    //		new GeneWayCodeSMSAlertLocalization(new ResourceBundles(), new Utils().getLocale(new
    // RequestUtils())),
    //		deviceDb.getCustomer().getContactInformation().getRegisteredPhoneNumber());

    // alertMechanism.setAlertMessage(new GeneWayCodeSMSAlertMessage(
    // deviceDb.getCustomer().getNickName(), code));
    try {
      ((EmailAlertMechanism) alertMechanism).getMimeMessage().setSubject(phoneNumber);
      alertMechanism.send();
    } catch (MessagingException e) {
      LOGGER.log(Level.FATAL, e.toString(), e);
    }
  }

  @Transactional
  void registerCustomer(Customer customer) {
    if (customer.getDevice() == null) {
      throw new IllegalArgumentException("The device cannot be null");
    }
    if (customer.getCredentials() == null) {
      throw new IllegalArgumentException("The credentials cannot be null");
    }

    customer.getDevice().setCode(code);
    customer.getDevice().setCodeCreation(LocalDateTime.now(clock));

    // allow new users when they register to connect to existing plans based on phone number
    // this is temporary. should be uncommented eventually.
    //		try{
    //			Customer customerDb = new
    // HibernateUtil().selectCustomerBasedOnPhoneNumber(customer.getContactInformation().getRegisteredPhoneNumber(), entityManager);
    //			if(customerDb.getContactInformation() != null)
    //				entityManager.get().remove(customerDb.getContactInformation());
    //			if(customerDb.getDevice() != null)
    //				entityManager.get().remove(customerDb.getDevice());
    //			entityManager.get().remove(customerDb); // delete this device
    //		}
    //		catch(NoResultException ex){
    //			//all good - device not in db
    //		}

    // TODO - if phone number associated with another device - check if it is inactive,
    // then delete entry. Otherwise, return an exception of used phone-number
    // this is bad - could be a privacy breach. Need to think of workaround.

    String hashedPassword = customer.getCredentials().hashPassword();

    customer.getCredentials().setHashedPassword(hashedPassword);

    entityManagerProvider.get().merge(customer);
    // do not return anything from Db at this stage. Only after logging in we do so.
    // customer.getDevice().setCode(null);// do not send code by mistake to client
  }
}
