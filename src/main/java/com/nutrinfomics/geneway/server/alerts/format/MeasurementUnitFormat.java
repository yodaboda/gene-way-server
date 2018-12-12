package com.nutrinfomics.geneway.server.alerts.format;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

@Singleton
public class MeasurementUnitFormat {

  private final ResourceBundles resourceBundles;

  @Inject
  public MeasurementUnitFormat(ResourceBundles resourceBundles) {
    this.resourceBundles = resourceBundles;
  }

  public String format(MeasurementUnit measurementUnit, Locale locale) {
    return resourceBundles.getMeasurementResource(measurementUnit, locale);
  }
}
