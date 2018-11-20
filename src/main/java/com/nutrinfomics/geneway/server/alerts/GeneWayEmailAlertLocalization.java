package com.nutrinfomics.geneway.server.alerts;

import java.util.Locale;

import javax.inject.Inject;

import com.geneway.alerts.localization.AlertLocalization;
import com.nutrinfomics.geneway.server.ResourceBundles;

public class GeneWayEmailAlertLocalization implements AlertLocalization {
	private ResourceBundles resourceBundles;
	private Locale locale;
	
	@Inject
	public GeneWayEmailAlertLocalization(ResourceBundles resourceBundles, Locale locale){
		this.resourceBundles = resourceBundles;
		this.locale = locale;
	}
	
	@Override
	public String localizeSubject(String subject) {
		return this.getResourceBundles().getGeneWayResource(subject, this.getLocale());
	}

	@Override
	public String localizeBody(String... body) {
		return this.getResourceBundles().getGeneWayResource(body[0], this.getLocale()) + 
													"\n\r https://gene-way.com";
	}

	protected Locale getLocale(){
		return locale;
	}
	
	protected ResourceBundles getResourceBundles(){
		return resourceBundles;
	}
}
