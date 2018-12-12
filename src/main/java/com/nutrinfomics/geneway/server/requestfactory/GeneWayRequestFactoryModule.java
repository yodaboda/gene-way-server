package com.nutrinfomics.geneway.server.requestfactory;

import javax.inject.Named;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;

/**
 * Guice Module for injecting {@code Request} related instances. This module depends on being
 * provided with a {@code Session} and a {@code Provider<EntityManager> }.
 *
 * @author Firas Swidan
 */
public class GeneWayRequestFactoryModule extends AbstractModule {

  @Override
  protected void configure() {
    // install(new AlertsModule());
    //		bind(ExceptionHandler.class).to(DefaultExceptionHandler.class);
    //		bind(ServiceLayerDecorator.class).to(GuiceServiceLayerDecorator.class);
    //		bind(GeneWayServiceLocator.class);
    //		bind(ResourceBundles.class);
    //		bind(Alert.class).to(EmailAlert.class).in(RequestScoped.class);
    //		bind(HibernateUtil.class).in(RequestScoped.class);
    //		bind(Alerts.class);
    //		bind(ScheduledAlert.class).in(RequestScoped.class);
    //		bind(AlertSender.class).to(DefaultEmailAlertSender.class).in(RequestScoped.class);
    //		bind(Utils.class);
    //		bind(RequestUtils.class);
    //		bind(ScheduledExecutorService.class).toInstance(Executors.newScheduledThreadPool(1));
  }

  @Provides
  @RequestScoped
  public @Named("dbSession") Session provideDbSession(
      Session clientSession, Provider<EntityManager> entityManager, HibernateUtil hibernateUtil) {
    return hibernateUtil.selectSession(clientSession.getSid(), entityManager);
  }

  //	@Provides
  //	@RequestScoped
  //	public AlertSpecification provideAlertLocalization(final AlertRecipient alertRecipient,
  //														final AlertSender alertSender,
  //														final AlertMessage alertMessage,
  //														final AlertLocalization alertLocalization) {
  //		return new AlertSpecification() {
  //
  //			@Override
  //			public AlertSender getAlertSender() {
  //				return alertSender;
  //			}
  //
  //			@Override
  //			public AlertRecipient getAlertRecipient() {
  //				return alertRecipient;
  //			}
  //
  //			@Override
  //			public AlertMessage getAlertMessage() {
  //				return alertMessage;
  //			}
  //
  //			@Override
  //			public AlertLocalization getAlertLocalization() {
  //				return alertLocalization;
  //			}
  //		};
  //	}
  //
  //	@Provides
  //	@RequestScoped
  //	public Locale provideLocale(Utils utils){
  //		return utils.getLocale();
  //	}
  //
  //	@Provides
  //	public AlertMessage provideAlertMessage(@Named("dbSession") Session session){
  //		return new AlertMessage(){
  //			@Override
  //			public String getSubject() {
  //				return "itsTimeToTakeYourMealTitle";
  //			}
  //
  //			@Override
  //			public String[] getBody() {
  //				return new String[]{"itsTimeToTakeYourMeal"};
  //			}
  //		};
  //	}
  //
  //	@Provides
  //	public AlertRecipient provideAlertRecipient(@Named("dbSession") Session session){
  //		AlertType alertType = session.getCustomer().getPlan().getPlanPreferences().isEmailAlerts() ?
  //								AlertType.E_MAIL : AlertType.SMS;
  //
  //		String recipient = null;
  //		ContactInformation contactInformation = session.getCustomer().getContactInformation();
  //		if(contactInformation != null) {
  //			if(alertType == AlertType.E_MAIL) {
  //				if(contactInformation.getEmails() != null && !contactInformation.getEmails().isEmpty()) {
  //					recipient = contactInformation.getEmails().get(0).getEmail();
  //				}
  //			}
  //			else {
  //				recipient = contactInformation.getRegisteredPhoneNumber();
  //			}
  //
  //		}
  //		return new DefaultAlertRecipient(recipient, alertType);
  //	}
  //
  //	@Provides
  //	public AlertLocalization provideAlertLocalization(@Named("dbSession") Session session,
  //														final ResourceBundles resourceBundles,
  //														final Locale locale){
  //		return new AlertLocalization() {
  //
  //			@Override
  //			public String localizeSubject(String subject) {
  //				return resourceBundles.getGeneWayResource(subject, this.getLocale());
  //			}
  //
  //			@Override
  //			public String localizeBody(String... body) {
  //				return resourceBundles.getGeneWayResource(body[0], this.getLocale()) +
  //															"\n\r https://gene-way.com";
  //			}
  //
  //			@Override
  //			public Locale getLocale() {
  //				return locale;
  //			}
  //		};
  //	}
  //
  //
  //	@Provides
  //	@RequestScoped
  //	public @Named("code") String provideCode(){
  //		SecureRandom random = new SecureRandom();
  //
  //		return new BigInteger(130, random).toString(32).substring(0, 6);
  //	}
  //
  //	/**
  //	 * Creates and reuses injecting JSR 303 Validator factory.
  //	 *
  //	 * @param injector the injector that will be used for the injection.
  //	 * @return The ValidatorFactory.
  //	 */
  //	@Provides
  //	@Singleton
  //	public ValidatorFactory getValidatorFactory(Injector injector) {
  //		//this is no good, because validator is singleton and fixed
  //		return Validation.byDefaultProvider().configure().constraintValidatorFactory(new
  // InjectingConstraintValidationFactory(injector)).buildValidatorFactory();
  //	}
  //	/**
  //	 * Creates and reuses injecting JSR 303 Validator.
  //	 *
  //	 * @param validatorFactory the ValidatorFactory to get the Validator from.
  //	 * @return the Validator.
  //	 */
  //	@Provides
  //	@Singleton
  //	public Validator getValidator(ValidatorFactory validatorFactory) {
  //		//this is no good, because validator is singleton and fixed
  //		return validatorFactory.getValidator();
  //	}
}
