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
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.alerts.Alert;
import com.nutrinfomics.geneway.server.alerts.Alerts;
import com.nutrinfomics.geneway.server.alerts.EmailAlert;
import com.nutrinfomics.geneway.server.alerts.ScheduledAlert;
import com.nutrinfomics.geneway.server.domain.contact.ContactInformation;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class GeneWayAlertsModule extends AbstractModule {

	@Override
	protected void configure() {

		requireBinding(Locale.class);
		requireBinding(Session.class); // @Named("dbSession")
		requireBinding(ResourceBundles.class);
		
		bind(AlertSender.class).to(DefaultEmailAlertSender.class).in(RequestScoped.class);

	}

	@Provides
	@RequestScoped
	public AlertSpecification provideAlertLocalization(final AlertRecipient alertRecipient,
			final AlertSender alertSender, final AlertMessage alertMessage, final AlertLocalization alertLocalization) {
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
				return "itsTimeToTakeYourMealTitle";
			}

			@Override
			public String[] getBody() {
				return new String[] { "itsTimeToTakeYourMeal" };
			}
		};
	}

	@Provides
	AlertRecipient provideAlertRecipient(@Named("dbSession") Session session) {
		AlertType alertType = session.getCustomer().getPlan().getPlanPreferences().isEmailAlerts() ? AlertType.E_MAIL
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
	AlertLocalization provideAlertLocalization(final ResourceBundles resourceBundles, final Locale locale) {
		return new AlertLocalization() {

			@Override
			public String localizeSubject(String subject) {
				return resourceBundles.getGeneWayResource(subject, this.getLocale());
			}

			@Override
			public String localizeBody(String... body) {
				return resourceBundles.getGeneWayResource(body[0], this.getLocale()) + "\n\r https://gene-way.com";
			}

			@Override
			public Locale getLocale() {
				return locale;
			}
		};
	}
}
