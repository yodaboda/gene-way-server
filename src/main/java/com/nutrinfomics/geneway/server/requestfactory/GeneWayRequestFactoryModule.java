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

package com.nutrinfomics.geneway.server.requestfactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Locale;

import javax.inject.Named;

import com.google.inject.AbstractModule;
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

  // TODO: Delete these providers
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

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */