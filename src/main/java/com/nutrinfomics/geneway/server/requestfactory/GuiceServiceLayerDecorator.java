package com.nutrinfomics.geneway.server.requestfactory;

import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.google.web.bindery.requestfactory.shared.Locator;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;

public class GuiceServiceLayerDecorator extends ServiceLayerDecorator {
	 /**
	* JSR 303 validator used to validate requested entities.
	*/
	private final Validator validator;
	private final Injector injector;
	private Utils utils;
	@Inject
	protected GuiceServiceLayerDecorator(final Injector injector, final Validator validator,
										Utils utils) {
		super();
		this.injector = injector;
		this.validator = validator;
		this.utils = utils;
	}
	@Override
	public <T extends Locator<?, ?>> T createLocator(Class<T> clazz) {
		return injector.getInstance(clazz);
	}
	@Override
	public Object createServiceInstance(Class<? extends RequestContext> requestContext) {
		Class<? extends ServiceLocator> serviceLocatorClass;
		if ((serviceLocatorClass = getTop().resolveServiceLocator(requestContext)) != null) {
			return injector.getInstance(serviceLocatorClass).getInstance(requestContext.getAnnotation(Service.class).value());
		} else {
			return null;
		}
	}
	/**
	 * Invokes JSR 303 validator on a given domain object.
	 *
	 * @param domainObject the domain object to be validated
	 * @param <T> the type of the entity being validated
	 * @return the violations associated with the domain object
	 */
	@Override
	public <T> Set<ConstraintViolation<T>> validate(T domainObject) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		MessageInterpolator defaultInterpolator = factory.getMessageInterpolator();
		Locale locale = utils.getLocale();
//				new Locale(RequestFactoryServlet
//				.getThreadLocalRequest().getHeader("X-GWT-Locale"));
		GeneWayLocaleMessageInterpolator interpolator = new GeneWayLocaleMessageInterpolator(defaultInterpolator, locale);
		Validator validator = factory.usingContext().messageInterpolator(interpolator).getValidator();
		return validator.validate(domainObject);
//		return validator.validate(domainObject);
	}
}
