package com.nutrinfomics.geneway.server.alerts.format;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.server.domain.plan.ArbitraryCycle;
import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.server.domain.plan.GeneralVaryingSnack;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

public class SnackFormatTest {

	  private FoodItemFormat mockFoodItemFormat;
	  private ResourceBundles mockResourceBundles;
	  
	  private SnackFormat snackFormat;
	  
	  @Before
	  public void setUp() {
		  mockFoodItemFormat = mock(FoodItemFormat.class);
		  mockResourceBundles = mock(ResourceBundles.class);
		  
		  snackFormat = new SnackFormat(mockFoodItemFormat, mockResourceBundles);
	  }
	  
	@Test
	public void format_AsExpected() {
		Locale locale = Locale.CHINESE;
		FoodItem foodItem = new FoodItem();
		Snack snack = new Snack(foodItem);
		String formatString = "is in chinese";
		when(mockFoodItemFormat.format(foodItem, locale)).thenReturn(formatString);
		String formattedString = snackFormat.format(snack, locale);
		
		assertEquals(formatString, formattedString);
	}

	@Test
	public void format_GeneraVaryingSnack_AsExpected() {
		Locale locale = Locale.forLanguageTag("en-IE");
		FoodItem foodItem = new FoodItem();
		foodItem.setCycle(new ArbitraryCycle(4));
		GeneralVaryingSnack generalVaryingSnack = new GeneralVaryingSnack();
		Snack snack = new Snack(foodItem);
		generalVaryingSnack.add(snack);
		String formatString = "Hii in Irish English";
		when(mockFoodItemFormat.format(foodItem, locale)).thenReturn(formatString);
		when(mockResourceBundles.getMiscBundleResource("days", locale)).thenReturn("GB days");
		String formattedString = snackFormat.format(generalVaryingSnack, locale);
		
		assertEquals(formatString + " - 4 GB days", formattedString);
	}
	
	@Test
	public void formatMeatSnack_AsExpected() {
		Locale locale = Locale.forLanguageTag("es");
		FoodItem foodItem = new FoodItem(89, MeasurementUnit.TEA_SPOON, FoodItemType.LAMB);
		foodItem.setCycle(new ArbitraryCycle(2));
		Snack snack = new Snack(foodItem);
		String snackFormatString = "Italian lamb in hindi";
		when(mockFoodItemFormat.format(any(), eq(locale))).thenReturn(snackFormatString);
		String daysFormatString = "days in portugesse";
		when(mockResourceBundles.getMiscBundleResource("days", locale)).thenReturn(daysFormatString);
		String formattedString = snackFormat.formatMeatSnack(snack, locale);
		
		assertEquals(snackFormatString + " - 2 " + daysFormatString, formattedString);
	}

}
