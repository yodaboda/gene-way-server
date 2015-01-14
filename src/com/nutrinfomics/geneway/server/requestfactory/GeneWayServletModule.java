package com.nutrinfomics.geneway.server.requestfactory;

import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;

public class GeneWayServletModule extends ServletModule {
	
	@Override
	protected void configureServlets() {
		install(new JpaPersistModule("domainPersistence"));  // like we saw earlier.

		filter("/*").through(PersistFilter.class);
		
		install(new GeneWayRequestFactoryModule());
		serve("/gwtRequest").with(GeneWayRequestFactoryServlet.class);
	}

}
