package com.nutrinfomics.geneway.server.alerts.format;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

public class FoodItemFormatTest {

  private FoodItemTypeFormat mockFoodItemTypeFormat;
  private MeasurementUnitFormat mockMeasurementUnitFormat;
  private FoodItemFormat foodItemFormat;

  @Before
  public void setup() {
    mockFoodItemTypeFormat = mock(FoodItemTypeFormat.class);
    mockMeasurementUnitFormat = mock(MeasurementUnitFormat.class);
    foodItemFormat = new FoodItemFormat(mockFoodItemTypeFormat, mockMeasurementUnitFormat);
  }

  @Test
  public void format_AsExpected() {

    FoodItemType foodItemType = FoodItemType.BEAN_GREEN;
    MeasurementUnit measurementUnit = MeasurementUnit.MICROGRAM;
    FoodItem foodItem = new FoodItem(79, measurementUnit, foodItemType);
    Locale locale = Locale.ENGLISH;

    String foodItemTypeFormatString = "Grean Bean";
    when(mockFoodItemTypeFormat.format(foodItemType, locale)).thenReturn(foodItemTypeFormatString);
    String measurementUnitFormatString = "MicroGram";
    when(mockMeasurementUnitFormat.format(measurementUnit, locale))
        .thenReturn(measurementUnitFormatString);

    String formattedOutput = foodItemFormat.format(foodItem, locale);
    assertEquals(foodItemTypeFormatString + " 79 " + measurementUnitFormatString, formattedOutput);
  }
}
