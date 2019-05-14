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

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.identifier.Identifier;
import com.nutrinfomics.geneway.server.requestfactory.request.AuthenticationService;
import com.nutrinfomics.geneway.shared.AccessConstants;

public class SecurityLocalizationDecorator extends ServiceLayerDecorator {

  private static final Logger LOGGER = LogManager.getLogger();

  @Inject Injector injector;

  @Override
  public Object invoke(Method domainMethod, Object... args) {
    if (!isAllowed(domainMethod)) {
      return doReport(domainMethod);
    }
    return super.invoke(domainMethod, args);
  }

  private boolean isAllowed(Method domainMethod) {
    try {
      if (userIsLoggedIn(RequestFactoryServlet.getThreadLocalRequest())) return true;
      else
        return domainMethod.equals(
                AuthenticationService.class.getMethod("authenticateCustomer", Customer.class))
            || domainMethod.equals(
                AuthenticationService.class.getMethod("register", Customer.class))
            || domainMethod.equals(
                AuthenticationService.class.getMethod("authenticateCode", Customer.class))
            || domainMethod.equals(
                AuthenticationService.class.getMethod("unlock", Identifier.class))
            || domainMethod.equals(
                AuthenticationService.class.getMethod(
                    "confirmValuationTermsOfService", String.class));
    } catch (NoSuchMethodException | SecurityException e) {
      LOGGER.log(Level.FATAL, e.toString(), e);
    }
    return false;
  }

  protected boolean userIsLoggedIn(HttpServletRequest req) {
    String sid = (String) req.getHeader(AccessConstants.SID.toString());
    String uuid = (String) req.getHeader(AccessConstants.UUID.toString());

    if (sid == null) return false;

    Session sessionDb = injector.getInstance(HibernateUtil.class).selectSession(sid);

    Customer customerDb = sessionDb.getCustomer();
    Device deviceDb = customerDb.getDevice();

    return (deviceDb.getUuid().equalsIgnoreCase(uuid) && sessionDb.getSid().equalsIgnoreCase(sid));
  }

  protected Object doReport(Method domainMethod) {
    // log.log(Level.INFO, "Operation {0}#{1} not allowed for user {2}",
    // new String[] {
    // domainMethod.getDeclaringClass().getCanonicalName(),
    // domainMethod.getName(),
    // requestProvider.get().getRemoteUser()
    // });

    return report("Operation not allowed: %s", domainMethod.getName());
  }

  // needed to support validation error message localization
  // @Override
  // public <U extends Object> Set<ConstraintViolation<U>> validate(U
  // domainObject) {
  // ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  // MessageInterpolator defaultInterpolator = factory.getMessageInterpolator();
  // Locale locale = Utils.getLocale();
  //// new Locale(RequestFactoryServlet
  //// .getThreadLocalRequest().getHeader("X-GWT-Locale"));
  // GeneWayLocaleMessageInterpolator interpolator = new
  // GeneWayLocaleMessageInterpolator(defaultInterpolator, locale);
  // Validator validator =
  // factory.usingContext().messageInterpolator(interpolator).getValidator();
  // return validator.validate(domainObject);
  // }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */