package com.nutrinfomics.geneway.server.requestfactory;

import javax.inject.Named;

import com.google.inject.Provides;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class GeneWayServletModule extends ServletModule {
	
	@Override
	protected void configureServlets() {
		install(new JpaPersistModule("domainPersistence"));  // like we saw earlier.

		filter("/*").through(PersistFilter.class);
		
		install(new GeneWayRequestFactoryModule());
		serve("/gwtRequest").with(GeneWayRequestFactoryServlet.class);
	}
	
	@Provides
	@RequestScoped
	@Named("emailAlertMechanismBody")
	public String provideBody(Session session){
		return "unimplemented";
	}
	
}
