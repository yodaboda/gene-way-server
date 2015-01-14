package com.nutrinfomics.geneway.server.requestfactory;

import java.lang.reflect.Method;

import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.requestfactory.request.AuthenticationService;

public class SecurityDecorator extends ServiceLayerDecorator {

	@Override
	public Object invoke(Method domainMethod, Object... args){
		if(!isAllowed(domainMethod)){
			return doReport(domainMethod);
		}
		return super.invoke(domainMethod, args);
	}
	
	 private boolean isAllowed(Method domainMethod) {
		try {
			return !GeneWayRequestFactoryServlet.isOnlyLoginAllowed() || 
					domainMethod.equals(AuthenticationService.class.getMethod("authenticateCustomer", Customer.class));
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	protected Object doReport(Method domainMethod) {
//		 log.log(Level.INFO, "Operation {0}#{1} not allowed for user {2}",
//		 new String[] {
//		 domainMethod.getDeclaringClass().getCanonicalName(),
//		 domainMethod.getName(),
//		 requestProvider.get().getRemoteUser()
//		 });
		  
		 return report("Operation not allowed: %s", domainMethod.getName());
		 } 
}
