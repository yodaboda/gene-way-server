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

import javax.inject.Named;

import com.geneway.alerts.AlertLocalization;
import com.geneway.alerts.AlertMessage;
import com.geneway.alerts.AlertRecipient;
import com.geneway.alerts.AlertSender;
import com.geneway.alerts.AlertSpecification;
import com.geneway.alerts.AlertType;
import com.geneway.alerts.impl.DefaultAlertRecipient;
import com.geneway.alerts.impl.DefaultEmailAlertSender;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.domain.contact.ContactInformation;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.utils.resources.ResourceBundles;

public class GeneWayAlertsModule extends AbstractModule {

  public static final String ALERT_MESSAGE_BODY = "itsTimeToTakeYourMeal";
  public static final String ALERT_MESSAGE_SUBJECT = "itsTimeToTakeYourMealTitle";

  @Override
  protected void configure() {

    requireBinding(Locale.class);
    requireBinding(Session.class); // @Named("dbSession")
    requireBinding(ResourceBundles.class);

    bind(AlertSender.class).to(DefaultEmailAlertSender.class).in(RequestScoped.class);
  }

  @Provides
  @RequestScoped
  public AlertSpecification provideAlertSpecification(
      final AlertRecipient alertRecipient,
      final AlertSender alertSender,
      final AlertMessage alertMessage,
      final AlertLocalization alertLocalization) {
    return new AlertSpecification() {

      @Override
      public AlertSender getAlertSender() {
        return alertSender;
      }

      @Override
      public AlertRecipient getAlertRecipient() {
        return alertRecipient;
      }

      @Override
      public AlertMessage getAlertMessage() {
        return alertMessage;
      }

      @Override
      public AlertLocalization getAlertLocalization() {
        return alertLocalization;
      }
    };
  }

  @Provides
  AlertMessage provideAlertMessage() {
    return new AlertMessage() {
      @Override
      public String getSubject() {
        return ALERT_MESSAGE_SUBJECT;
      }

      @Override
      public String[] getBody() {
        return new String[] {ALERT_MESSAGE_BODY};
      }
    };
  }

  @Provides
  AlertRecipient provideAlertRecipient(@Named("dbSession") Session session) {
    AlertType alertType =
        session.getCustomer().getPlan().getPlanPreferences().isEmailAlerts()
            ? AlertType.E_MAIL
            : AlertType.SMS;

    String recipient = null;
    ContactInformation contactInformation = session.getCustomer().getContactInformation();
    if (contactInformation != null) {
      if (alertType == AlertType.E_MAIL) {
        if (contactInformation.getEmails() != null && !contactInformation.getEmails().isEmpty()) {
          recipient = contactInformation.getEmails().get(0).getEmail();
        }
      } else {
        recipient = contactInformation.getRegisteredPhoneNumber();
      }
    }
    return new DefaultAlertRecipient(recipient, alertType);
  }

  @Provides
  AlertLocalization provideAlertLocalization(
      final ResourceBundles resourceBundles, final Locale locale) {
    return new AlertLocalization() {

      @Override
      public String localizeSubject(String subject) {
        return resourceBundles.getGeneWayResource(subject, this.getLocale());
      }

      @Override
      public String localizeBody(String... body) {
        return resourceBundles.getGeneWayResource(body[0], this.getLocale())
            + "\n\r https://gene-way.com";
      }

      @Override
      public Locale getLocale() {
        return locale;
      }
    };
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */
