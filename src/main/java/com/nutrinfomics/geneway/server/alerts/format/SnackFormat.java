package com.nutrinfomics.geneway.server.alerts.format;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.server.domain.plan.GeneralVaryingSnack;
import com.nutrinfomics.geneway.server.domain.plan.Snack;

@Singleton
public class SnackFormat {
  private final FoodItemFormat foodItemFormat;
  private final ResourceBundles resourceBundles;

  @Inject
  public SnackFormat(FoodItemFormat foodItemFormat, ResourceBundles resourceBundles) {
    this.foodItemFormat = foodItemFormat;
    this.resourceBundles = resourceBundles;
  }

  //TODO: Use decorator design pattern for classes in this package
  public String format(Snack snack, Locale locale) {
    if (snack instanceof GeneralVaryingSnack) {
      return formatGeneralVaryingSnack((GeneralVaryingSnack) snack, locale);
    }
    Collection<FoodItem> foodItems = snack.getFoodItems();
    StringBuilder bld = new StringBuilder();
    for (FoodItem foodItem : foodItems) {
      bld.append(foodItemFormat.format(foodItem, locale));
      bld.append(" + ");
    }
    String s = bld.toString();
    return s.isEmpty() ? s : s.substring(0, s.length() - 3); // string ends with " + "
  }

  private String formatGeneralVaryingSnack(GeneralVaryingSnack snack, Locale locale) {
    StringBuilder bld = new StringBuilder();
    for (Snack snck : snack.getSnacks()) {
      int minCycleLength = Integer.MAX_VALUE;
      for (FoodItem foodItem : snck.getFoodItems()) {
        minCycleLength = Math.min(minCycleLength, foodItem.getCycle().getCycleLength());
      }
      bld.append(format(snck, locale));
      bld.append(" - ");
      bld.append(NumberFormat.getIntegerInstance(locale).format(minCycleLength));
      bld.append(" ");
      bld.append(resourceBundles.getMiscBundleResource("days", locale));
      bld.append(System.getProperty("line.separator"));
    }
    String s = bld.toString();
    return s.isEmpty() ? s : s.substring(0, s.length() - 1); // string ends with "line.separator"
  }

  public String formatMeatSnack(Snack snack, Locale locale) {
    Collection<FoodItem> foodItems = snack.getFoodItems();
    StringBuilder bld = new StringBuilder();
    for (FoodItem foodItem : foodItems) {
    	bld.append(          foodItemFormat.format(
                new FoodItem(
                        foodItem.getAmount(), foodItem.getMeasurementUnit(), foodItem.getFoodType()),
                    locale));
    	bld.append(" - ");
    	bld.append(NumberFormat.getIntegerInstance(locale).format(foodItem.getCycle().getCycleLength()));
    	bld.append(" ");
    	bld.append(resourceBundles.getMiscBundleResource("days", locale));
    	bld.append(System.getProperty("line.separator"));
    }
    String s = bld.toString();
    return s.isEmpty() ? s : s.substring(0, s.length() - 1); // string ends with "\n"
  }
}
