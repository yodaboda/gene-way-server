package com.nutrinfomics.geneway.server.alerts.format;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.SnackProperty;

@Singleton
public class SnackPropertyFormat {

  private final ResourceBundles resourceBundles;

  @Inject
  public SnackPropertyFormat(ResourceBundles resourceBundles) {
    this.resourceBundles = resourceBundles;
  }

  public String format(SnackProperty snackProperty, Locale locale) {
    return resourceBundles.getMiscResource(snackProperty.toString(), locale);
  }
}
