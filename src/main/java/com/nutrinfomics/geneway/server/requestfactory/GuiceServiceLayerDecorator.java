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
import com.google.web.bindery.requestfactory.shared.ServiceLocator;

public class GuiceServiceLayerDecorator extends ServiceLayerDecorator {
  /** JSR 303 validator used to validate requested entities. */
  // TODO: delete this field
  //  private final Validator validator;

  private final Injector injector;

  private Locale requestLocale;

  @Inject
  protected GuiceServiceLayerDecorator(
      final Injector injector,
      // final Validator validator,
      Locale requestLocale) {
    super();
    this.injector = injector;
    //    this.validator = validator;
    this.requestLocale = requestLocale;
  }

  @Override
  public <T extends Locator<?, ?>> T createLocator(Class<T> clazz) {
    return injector.getInstance(clazz);
  }

  @Override
  public <T extends ServiceLocator> T createServiceLocator(Class<T> clazz) {
    return injector.getInstance(clazz);
  }

  //  @Override
  //  public Object createServiceInstance(Class<? extends RequestContext> requestContext) {
  //    Class<? extends ServiceLocator> serviceLocatorClass;
  //    if ((serviceLocatorClass = getTop().resolveServiceLocator(requestContext)) != null) {
  //      return injector
  //          .getInstance(serviceLocatorClass)
  //          .getInstance(requestContext.getAnnotation(Service.class).value());
  //    } else {
  //      return null;
  //    }
  //  }
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

    GeneWayLocaleMessageInterpolator interpolator =
        new GeneWayLocaleMessageInterpolator(defaultInterpolator, requestLocale);
    Validator domainObjectValidator =
        factory.usingContext().messageInterpolator(interpolator).getValidator();
    return domainObjectValidator.validate(domainObject);
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */