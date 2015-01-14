package com.nutrinfomics.geneway.server.requestfactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.web.bindery.requestfactory.server.DefaultExceptionHandler;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;

public class GeneWayRequestFactoryModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ExceptionHandler.class).to(DefaultExceptionHandler.class);
		bind(ServiceLayerDecorator.class).to(GuiceServiceLayerDecorator.class);
		bind(GeneWayServiceLocator.class);	
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
		return validatorFactory.getValidator();
	}
}
