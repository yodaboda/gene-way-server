package com.nutrinfomics.geneway.server.requestfactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;

/**
 * Guice Module for injecting {@code Request} related instances. This module depends on being
 * provided with a {@code Session} and a {@code Provider<EntityManager> }.
 *
 * @author Firas Swidan
 */
public class GeneWayRequestFactoryModule extends AbstractModule {

  @Override
  protected void configure() {
    requireBinding(SecureRandom.class);
    requireBinding(Utils.class);
    requireBinding(RequestUtils.class);
  }

  @Provides
  @RequestScoped
  public @Named("code") String provideCode(SecureRandom random) {
    return new BigInteger(130, random).toString(32).substring(0, 6);
  }

  @Provides
  @RequestScoped
  public Locale provideLocale(Utils utils) {
    return utils.getLocale();
  }

  //TODO: Delete these providers
//  /**
//   * Creates and reuses injecting JSR 303 Validator factory.
//   *
//   * @param injector the injector that will be used for the injection.
//   * @return The ValidatorFactory.
//   */
//  @Provides
//  @Singleton
//  public ValidatorFactory getValidatorFactory(Injector injector) {
//    // this is no good, because validator is singleton and fixed
//    return Validation.byDefaultProvider()
//        .configure()
//        .constraintValidatorFactory(new InjectingConstraintValidationFactory(injector))
//        .buildValidatorFactory();
//  }
//
//  /**
//   * Creates and reuses injecting JSR 303 Validator.
//   *
//   * @param validatorFactory the ValidatorFactory to get the Validator from.
//   * @return the Validator.
//   */
//  @Provides
//  @Singleton
//  public Validator getValidator(ValidatorFactory validatorFactory) {
//    // this is no good, because validator is singleton and fixed
//    return validatorFactory.getValidator();
//  }
}
