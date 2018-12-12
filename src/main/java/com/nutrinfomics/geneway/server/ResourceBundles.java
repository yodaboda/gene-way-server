package com.nutrinfomics.geneway.server;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.inject.Singleton;

import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;
import com.nutrinfomics.geneway.shared.SupplementType;

/**
 * This class provides an interface for accessing all the resource bundles needed for
 * internationalization.
 *
 * @author firas1
 */
@Singleton
public class ResourceBundles {
  private static final String PATH = "com/nutrinfomics/geneway/shared/constants/";

  private static String getResourcePath(String fileName) {
    return PATH + fileName;
  }

  public String getFoodItemResource(FoodItemType foodItemType, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("FoodItemTypeConstants"), locale)
        .getString(foodItemType.toString());
  }

  public String getMeasurementResource(MeasurementUnit measurementUnit, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("MeasurementsConstants"), locale)
        .getString(measurementUnit.toString());
  }

  public String getSupplementsResource(SupplementType supplement, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("SupplementsConstants"), locale)
        .getString(supplement.toString());
  }

  public String getMiscResource(String string, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("MiscConstants"), locale).getString(string);
  }

  public String getMiscBundleResource(String string, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("MiscBundle"), locale).getString(string);
  }

  public String getActivitiesResource(String string, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("Activities"), locale).getString(string);
  }

  public String getGeneWayResource(String string, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("GeneWayConstants"), locale).getString(string);
  }

  public String getOpeningHoursResource(String string, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("OpeningHours"), locale).getString(string);
  }

  public String getCongratulationsResource(String string, Locale locale) {
    return ResourceBundle.getBundle(getResourcePath("Congratulations"), locale).getString(string);
  }
}
