package com.nutrinfomics.geneway.server;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.MissingResourceException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

public class ResourceBundlesTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
	@Test
	public void testGetFoodItemResource() {
		String appleEnglishLocalized = ResourceBundles.getFoodItemResource(FoodItemType.APPLE, Locale.ENGLISH);
		assertEquals("apple", appleEnglishLocalized);
	}

	@Test
	public void testGetFoodItemResourceArabic() {
		String bananaArabicLocalized = ResourceBundles.getFoodItemResource(FoodItemType.BANANA, Locale.forLanguageTag("ar"));
		assertEquals("موز", bananaArabicLocalized);
	}

	@Test
	public void testGetFoodItemResourceHebrew() {
		String eggHebrewLocalized = ResourceBundles.getFoodItemResource(FoodItemType.EGG, Locale.forLanguageTag("iw"));
		assertEquals("ביצה", eggHebrewLocalized);
	}

	@Test
	public void testGetFoodItemResourceSwedish() {
		String peachSwedishLocalized = ResourceBundles.getFoodItemResource(FoodItemType.PEACH, Locale.forLanguageTag("sv"));
		assertEquals("peach", peachSwedishLocalized);
	}
	
	@Test
	public void testGetMeasurementResource() {
		thrown.expect(MissingResourceException.class);
		thrown.expectMessage("Can't find resource for bundle java.util.PropertyResourceBundle, key CENTIMETRE");
		ResourceBundles.getMeasurementResource(MeasurementUnit.CENTIMETRE, Locale.forLanguageTag("no"));
	}

	@Test
	public void testGetMeasurementResourceArabic() {
		String teaSpoonArabicLocalized = ResourceBundles.getMeasurementResource(MeasurementUnit.TEA_SPOON, Locale.forLanguageTag("ar"));
		assertEquals("ملعقة صغيرة", teaSpoonArabicLocalized);
	}

	@Test
	public void testGetSupplementsResource() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMiscResource() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMiscBundleResource() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActivitiesResource() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGeneWayResource() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOpeningHoursResource() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCongratulationsResource() {
		fail("Not yet implemented");
	}

}
