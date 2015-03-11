package com.nutrinfomics.geneway.server.requestfactory;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Device;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.requestfactory.request.AuthenticationService;
import com.nutrinfomics.geneway.shared.AccessConstants;

public class SecurityLocalizationDecorator extends ServiceLayerDecorator {

	@Inject
	Provider<EntityManager> em;

	@Override
	public Object invoke(Method domainMethod, Object... args) {
		if (!isAllowed(domainMethod)) {
			return doReport(domainMethod);
		}
		return super.invoke(domainMethod, args);
	}

	private boolean isAllowed(Method domainMethod) {
		try {
			if (userIsLoggedIn(RequestFactoryServlet.getThreadLocalRequest()))
				return true;
			else
				return domainMethod.equals(AuthenticationService.class
						.getMethod("authenticateCustomer", Customer.class))
						|| domainMethod.equals(AuthenticationService.class
								.getMethod("register", Session.class))
						|| domainMethod.equals(AuthenticationService.class
								.getMethod("authenticateCode", Session.class));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	protected boolean userIsLoggedIn(HttpServletRequest req) {
		String sid = (String) req.getHeader(AccessConstants.SID.toString());
		String uuid = (String) req.getHeader(AccessConstants.UUID.toString());

		if (sid == null)
			return false;

		Session sessionDb = new HibernateUtil().selectSession(sid, em);

		Customer customerDb = sessionDb.getCustomer();
		Device deviceDb = customerDb.getDevice();

		return (deviceDb.getUuid().equalsIgnoreCase(uuid) && sessionDb.getSid()
				.equalsIgnoreCase(sid));
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
	
	//needed to support validation error message localization
//	@Override
//	public <U extends Object> Set<ConstraintViolation<U>> validate(U domainObject) {
//		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//		MessageInterpolator defaultInterpolator = factory.getMessageInterpolator();
//		Locale locale = Utils.getLocale();
////				new Locale(RequestFactoryServlet
////				.getThreadLocalRequest().getHeader("X-GWT-Locale"));
//		GeneWayLocaleMessageInterpolator interpolator = new GeneWayLocaleMessageInterpolator(defaultInterpolator, locale);
//		Validator validator = factory.usingContext().messageInterpolator(interpolator).getValidator();
//		return validator.validate(domainObject);
//	}
}
