package com.nutrinfomics.geneway.server.alerts.codeSMS;

import java.util.Locale;

import javax.inject.Inject;

import com.geneway.alerts.localization.AlertLocalization;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.alerts.GeneWayEmailAlertLocalization;
import com.nutrinfomics.geneway.server.domain.EntityBase;

public class GeneWayCodeSMSAlertLocalization extends GeneWayEmailAlertLocalization implements
		AlertLocalization {

	@Inject
	public GeneWayCodeSMSAlertLocalization(ResourceBundles resourceBundles,
			Locale locale) {
		super(resourceBundles, locale);
	}

	@Override
	public String localizeBody(String... body) {
		String nickName = body[0];
		String code = body[1];
		return this.getResourceBundles().getGeneWayResource("dear", this.getLocale()) + " " + nickName + ". " + 
				this.getResourceBundles().getGeneWayResource("yourCodeIs", this.getLocale()) + ": " + code;
	}
}
