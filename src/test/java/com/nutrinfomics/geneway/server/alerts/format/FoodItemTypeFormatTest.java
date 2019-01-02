package com.nutrinfomics.geneway.server.alerts.format;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.FoodItemType;

public class FoodItemTypeFormatTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  private ResourceBundles mockResourceBundles;
  private FoodItemTypeFormat foodItemTypeFormat;

  @Before
  public void setup() {
    mockResourceBundles = mock(ResourceBundles.class);
  }

  @Test
  public void format_AsExpected() {
    FoodItemType foodItemType = FoodItemType.BEEF_TENDERLOIN;
    Locale arLanguage = Locale.forLanguageTag("ar");
    String resourceResult = "فيليه";

    foodItemTypeFormat = new FoodItemTypeFormat(mockResourceBundles);

    when(mockResourceBundles.getFoodItemResource(foodItemType, arLanguage))
        .thenReturn(resourceResult);

    assertEquals(resourceResult, foodItemTypeFormat.format(foodItemType, arLanguage));
  }

  @Test
  public void format_nullFoodItem() {
    FoodItemType foodItemType = null;
    Locale arLanguage = Locale.GERMAN;

    foodItemTypeFormat = new FoodItemTypeFormat(mockResourceBundles);

    when(mockResourceBundles.getFoodItemResource(foodItemType, arLanguage))
        .thenThrow(NullPointerException.class);

    thrown.expect(NullPointerException.class);
    foodItemTypeFormat.format(foodItemType, arLanguage);
  }
}
