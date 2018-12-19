package com.nutrinfomics.geneway.server.requestfactory;

import com.google.inject.Scopes;
import com.google.inject.servlet.RequestScoped;

public class TestGeneWayAlertsModule extends GeneWayAlertsModule {

	@Override
	protected void configure() {
		//TODO: Figure out a way to deal with 
		// No scope is bound to com.google.inject.servlet.RequestScoped
		bindScope(RequestScoped.class, Scopes.SINGLETON);
	}
}
