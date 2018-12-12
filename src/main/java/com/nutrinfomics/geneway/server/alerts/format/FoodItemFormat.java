package com.nutrinfomics.geneway.server.alerts.format;

import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.nutrinfomics.geneway.server.domain.plan.FoodItem;

// TODO: Perhaps move these classes to a the shared project
@Singleton
public class FoodItemFormat {
  private final FoodItemTypeFormat foodItemTypeFormat;
  private final MeasurementUnitFormat measurementUnitFormat;

  @Inject
  public FoodItemFormat(
      FoodItemTypeFormat foodItemTypeFormat, MeasurementUnitFormat measurementUnitFormat) {
    this.foodItemTypeFormat = foodItemTypeFormat;
    this.measurementUnitFormat = measurementUnitFormat;
  }

  public String format(FoodItem foodItem, Locale locale) {
    return foodItemTypeFormat.format(foodItem.getFoodType(), locale)
        + " "
        + NumberFormat.getInstance(locale).format(foodItem.getAmount())
        + " "
        + measurementUnitFormat.format(foodItem.getMeasurementUnit(), locale);
  }
}
