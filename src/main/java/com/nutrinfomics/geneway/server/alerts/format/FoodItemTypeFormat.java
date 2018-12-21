package com.nutrinfomics.geneway.server.alerts.format;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.FoodItemType;

//TODO: Use decorator design pattern for classes in this package
@Singleton
public class FoodItemTypeFormat {

  private final ResourceBundles resourceBundles;

  @Inject
  public FoodItemTypeFormat(ResourceBundles resourceBundles) {
    this.resourceBundles = resourceBundles;
  }

  public String format(FoodItemType foodItemType, Locale locale) {
    return resourceBundles.getFoodItemResource(foodItemType, locale);
  }
}
