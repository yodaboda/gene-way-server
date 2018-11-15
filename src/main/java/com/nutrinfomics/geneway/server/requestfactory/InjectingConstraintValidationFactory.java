package com.nutrinfomics.geneway.server.requestfactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import com.google.inject.Injector;

public class InjectingConstraintValidationFactory implements
		ConstraintValidatorFactory {

	/**
	 * The entry point injector that is used to inject different kind of dependencies.
	 */
	private final Injector injector;
	/**
	 * Creates a new InjectingConstraintValidationFactory by using the injector.
	 *
	 * @param injector the injector that will be used for the injection.
	 */
	public InjectingConstraintValidationFactory(Injector injector) {
		this.injector = injector;
	}
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> tClass) {
		return injector.getInstance(tClass);
	}
}
