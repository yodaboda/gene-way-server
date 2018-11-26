package com.nutrinfomics.geneway.server.requestfactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.geneway.alerts.AlertLocalization;
import com.geneway.alerts.AlertMechanism;
import com.geneway.alerts.AlertMessage;
import com.geneway.alerts.AlertRecipient;
import com.geneway.alerts.AlertSender;
import com.geneway.alerts.impl.DefaultEmailAlertSender;
import com.geneway.alerts.impl.EmailAlertRecipient;
import com.geneway.alerts.injection.AlertsModule;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.web.bindery.requestfactory.server.DefaultExceptionHandler;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.alerts.Alert;
import com.nutrinfomics.geneway.server.alerts.EmailAlert;
import com.nutrinfomics.geneway.server.alerts.ScheduledAlert;
import com.nutrinfomics.geneway.server.alerts.ScheduledAlert.AlertType;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.contact.ContactInformation;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class GeneWayRequestFactoryModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new AlertsModule());
		bind(ExceptionHandler.class).to(DefaultExceptionHandler.class);
		bind(ServiceLayerDecorator.class).to(GuiceServiceLayerDecorator.class);
		bind(GeneWayServiceLocator.class);
		bind(ResourceBundles.class);
		bind(Alert.class).to(EmailAlert.class);
	}

	@Provides
	public @Named("dbSession") Session provideDbSession(Session clientSession, 
														Provider<EntityManager> entityManager){
		return new HibernateUtil().selectSession(clientSession.getSid(), 
												 entityManager);
	}
	
	@Provides
	public Locale provideLocale(){
		return new Utils().getLocale(new RequestUtils());
	}
		
	@Provides
	public AlertMessage provideAlertMessage(@Named("dbSession") Session session){
		return new AlertMessage(){
			@Override
			public String getSubject() {
				return "itsTimeToTakeYourMealTitle";
			}

			@Override
			public String[] getBody() {
				return new String[]{"itsTimeToTakeYourMeal"};
			}
		};
	}
	
	@Provides
	public AlertRecipient provideAlertRecipient(@Named("dbSession") Session session){
		String email = null;
		ContactInformation contactInformation = session.getCustomer().getContactInformation();
		if(contactInformation != null && contactInformation.getEmails() != null 
				&& !contactInformation.getEmails().isEmpty()){
			email = contactInformation.getEmails().get(0).getEmail();
		}
		return new EmailAlertRecipient(email);
	}
	
	@Provides
	public AlertLocalization provideAlertLocalization(@Named("dbSession") Session session,
														final ResourceBundles resourceBundles,
														final Locale locale){
		return new AlertLocalization() {
			
			@Override
			public String localizeSubject(String subject) {
				return resourceBundles.getGeneWayResource(subject, this.getLocale());
			}
			
			@Override
			public String localizeBody(String... body) {
				return resourceBundles.getGeneWayResource(body[0], this.getLocale()) + 
															"\n\r https://gene-way.com";
			}
			
			@Override
			public Locale getLocale() {
				return locale;
			}
		};
	}
		
	@Provides
	@Named("phoneNumber") 
	String providePhoneNumber(@Named("dbSession") Session session){
		return session.getCustomer().getContactInformation().getRegisteredPhoneNumber();
	}
	
	@Provides
	public AlertSender provideAlertSender(){
		return new DefaultEmailAlertSender();
	}
		
	@Provides
	public ScheduledAlert provideScheduledAlert(Alert alert){
		return new ScheduledAlert(alert);
	}
	
	@Provides
	@Named("code")
	@RequestScoped
	public String provideCode(){
		SecureRandom random = new SecureRandom();

		String code = new BigInteger(130, random).toString(32).substring(0, 6);
		code = "123456"; //TODO: comment this line out
		return code;
	}
	
	/**
	 * Creates and reuses injecting JSR 303 Validator factory.
	 *
	 * @param injector the injector that will be used for the injection.
	 * @return The ValidatorFactory.
	 */
	@Provides
	@Singleton
	public ValidatorFactory getValidatorFactory(Injector injector) {
		//this is no good, because validator is singleton and fixed 
		return Validation.byDefaultProvider().configure().constraintValidatorFactory(new InjectingConstraintValidationFactory(injector)).buildValidatorFactory();
	}
	/**
	 * Creates and reuses injecting JSR 303 Validator.
	 *
	 * @param validatorFactory the ValidatorFactory to get the Validator from.
	 * @return the Validator.
	 */
	@Provides
	@Singleton
	public Validator getValidator(ValidatorFactory validatorFactory) {
		//this is no good, becasue validator is singleton and fixed
		return validatorFactory.getValidator();
	}
}
