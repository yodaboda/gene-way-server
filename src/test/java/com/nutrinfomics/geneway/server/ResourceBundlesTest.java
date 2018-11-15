package com.nutrinfomics.geneway.server;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.MissingResourceException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;
import com.nutrinfomics.geneway.shared.SupplementType;

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
	public void testGetFoodItemResourceNull() {
		thrown.expect(NullPointerException.class);
		ResourceBundles.getFoodItemResource(null, Locale.getDefault());
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
	public void testGetSupplementsResourceChinese() {
		String zincChineseLocalized = ResourceBundles.getSupplementsResource(SupplementType.ZINC, Locale.CHINESE);
		assertEquals("Zinc", zincChineseLocalized);
	}

	@Test
	public void testGetSupplementsResourceGerman() {
		String vitaminDGermanLocalized = ResourceBundles.getSupplementsResource(SupplementType.VITAMIN_D, Locale.GERMAN);
		assertEquals("Vitamin D", vitaminDGermanLocalized);
	}

	@Test
	public void testGetSupplementsResourceFrench() {
		String calciumFrenchLocalized =  ResourceBundles.getSupplementsResource(SupplementType.CALCIUM, Locale.FRENCH);
		assertEquals("Calcium", calciumFrenchLocalized);
	}

	
	@Test
	public void testGetMiscResource() {
		String energyRussianLocalized = ResourceBundles.getMiscResource("ENERGY", Locale.forLanguageTag("ru"));
		assertEquals("energy", energyRussianLocalized);
	}

	@Test
	public void testGetMiscResourceMissing() {
		thrown.expect(MissingResourceException.class);
		thrown.expectMessage("Can't find resource for bundle java.util.PropertyResourceBundle, key Other");
		ResourceBundles.getMiscResource("Other", Locale.getDefault());
	}

	@Test
	public void testGetMiscBundleResource() {
		String dinnerHebrewLocalized = ResourceBundles.getMiscBundleResource("snack", Locale.forLanguageTag("iw"));
		assertEquals("מנה", dinnerHebrewLocalized);
	}

	@Test
	public void testGetMiscBundleResourceMissing() {
		thrown.expect(MissingResourceException.class);
		thrown.expectMessage("Can't find resource for bundle java.util.PropertyResourceBundle, key weeks");
		ResourceBundles.getMiscBundleResource("weeks", Locale.forLanguageTag("iw"));
	}

	@Test
	public void testGetActivitiesResource() {
		String sunbathingArabicLocalized = ResourceBundles.getActivitiesResource("SUNBATHING", Locale.forLanguageTag("ar"));
		assertEquals("التعرض للشمس - التعرض للشمس خلال الساعات الصحية لمدة ساعة يوميا", sunbathingArabicLocalized);
	}

	@Test
	public void testGetGeneWayResource() {
		String weightItalianLocalized = ResourceBundles.getGeneWayResource("weight", Locale.ITALIAN);
		assertEquals("Weight", weightItalianLocalized);
	}

	@Test
	public void testGetGeneWayResourceMissing() {
		thrown.expect(MissingResourceException.class);
		thrown.expectMessage("Can't find resource for bundle java.util.PropertyResourceBundle, key errors");
		ResourceBundles.getGeneWayResource("errors", Locale.KOREAN);
	}

	@Test
	public void testGetOpeningHoursResource() {
		String sundayJapaneseLocalized = ResourceBundles.getOpeningHoursResource("Sunday", Locale.JAPANESE);
		assertEquals("Sunday", sundayJapaneseLocalized);
	}

	@Test
	public void testGetOpeningHoursResourceMissing() {
		thrown.expect(MissingResourceException.class);
		thrown.expectMessage("Can't find resource for bundle java.util.PropertyResourceBundle, key address");
		ResourceBundles.getOpeningHoursResource("address", Locale.CANADA);
	}

	@Test
	public void testGetCongratulationsResource() {
		String happyNewYearSimplifiedChineseLocalized = ResourceBundles.getCongratulationsResource("happy new year", Locale.SIMPLIFIED_CHINESE);
		assertEquals("Happy New Year", happyNewYearSimplifiedChineseLocalized);
	}

	@Test
	public void testGetCongratulationsResourceMissing() {
		thrown.expect(MissingResourceException.class);
		thrown.expectMessage("Can't find resource for bundle java.util.PropertyResourceBundle, key name");
		ResourceBundles.getCongratulationsResource("name", Locale.forLanguageTag("es"));
	}

}
