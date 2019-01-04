package com.nutrinfomics.geneway.server;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.MissingResourceException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;
import com.nutrinfomics.geneway.shared.SupplementType;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class ResourceBundlesTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testGetFoodItemResource() {
    String appleEnglishLocalized =
        new ResourceBundles().getFoodItemResource(FoodItemType.APPLE, Locale.ENGLISH);
    assertEquals("apple", appleEnglishLocalized);
  }

  @Test
  public void testGetFoodItemResourceArabic() {
    String bananaArabicLocalized =
        new ResourceBundles().getFoodItemResource(FoodItemType.BANANA, Locale.forLanguageTag("ar"));
    assertEquals("موز", bananaArabicLocalized);
  }

  @Test
  public void testGetFoodItemResourceHebrew() {
    String eggHebrewLocalized =
        new ResourceBundles().getFoodItemResource(FoodItemType.EGG, Locale.forLanguageTag("iw"));
    assertEquals("ביצה", eggHebrewLocalized);
  }

  @Test
  public void testGetFoodItemResourceSwedish() {
    String peachSwedishLocalized =
        new ResourceBundles().getFoodItemResource(FoodItemType.PEACH, Locale.forLanguageTag("sv"));
    assertEquals("peach", peachSwedishLocalized);
  }

  @Test
  public void testGetFoodItemResourceNull() {
    thrown.expect(NullPointerException.class);
    new ResourceBundles().getFoodItemResource(null, Locale.getDefault());
  }

  @Test
  public void testGetMeasurementResource() {
    thrown.expect(MissingResourceException.class);
    thrown.expectMessage(
        "Can't find resource for bundle java.util.PropertyResourceBundle, key CENTIMETRE");
    new ResourceBundles()
        .getMeasurementResource(MeasurementUnit.CENTIMETRE, Locale.forLanguageTag("no"));
  }

  @Test
  public void testGetMeasurementResourceArabic() {
    String teaSpoonArabicLocalized =
        new ResourceBundles()
            .getMeasurementResource(MeasurementUnit.TEA_SPOON, Locale.forLanguageTag("ar"));
    assertEquals("ملعقة صغيرة", teaSpoonArabicLocalized);
  }

  @Test
  public void testGetSupplementsResourceChinese() {
    String zincChineseLocalized =
        new ResourceBundles().getSupplementsResource(SupplementType.ZINC, Locale.CHINESE);
    assertEquals("Zinc", zincChineseLocalized);
  }

  @Test
  public void testGetSupplementsResourceGerman() {
    String vitaminDGermanLocalized =
        new ResourceBundles().getSupplementsResource(SupplementType.VITAMIN_D, Locale.GERMAN);
    assertEquals("Vitamin D", vitaminDGermanLocalized);
  }

  @Test
  public void testGetSupplementsResourceFrench() {
    String calciumFrenchLocalized =
        new ResourceBundles().getSupplementsResource(SupplementType.CALCIUM, Locale.FRENCH);
    assertEquals("Calcium", calciumFrenchLocalized);
  }

  @Test
  public void testGetMiscResource() {
    String energyRussianLocalized =
        new ResourceBundles().getMiscResource("ENERGY", Locale.forLanguageTag("ru"));
    assertEquals("energy", energyRussianLocalized);
  }

  @Test
  public void testGetMiscResourceMissing() {
    thrown.expect(MissingResourceException.class);
    thrown.expectMessage(
        "Can't find resource for bundle java.util.PropertyResourceBundle, key Other");
    new ResourceBundles().getMiscResource("Other", Locale.getDefault());
  }

  @Test
  public void testGetMiscBundleResource() {
    String dinnerHebrewLocalized =
        new ResourceBundles().getMiscBundleResource("snack", Locale.forLanguageTag("iw"));
    assertEquals("מנה", dinnerHebrewLocalized);
  }

  @Test
  public void testGetMiscBundleResourceMissing() {
    thrown.expect(MissingResourceException.class);
    thrown.expectMessage(
        "Can't find resource for bundle java.util.PropertyResourceBundle, key weeks");
    new ResourceBundles().getMiscBundleResource("weeks", Locale.forLanguageTag("iw"));
  }

  @Test
  public void testGetActivitiesResource() {
    String sunbathingArabicLocalized =
        new ResourceBundles().getActivitiesResource("SUNBATHING", Locale.forLanguageTag("ar"));
    assertEquals(
        "التعرض للشمس - التعرض للشمس خلال الساعات الصحية لمدة ساعة يوميا",
        sunbathingArabicLocalized);
  }

  @Test
  public void testGetGeneWayResource() {
    String weightItalianLocalized =
        new ResourceBundles().getGeneWayResource("weight", Locale.ITALIAN);
    assertEquals("Weight", weightItalianLocalized);
  }

  @Test
  public void testGetGeneWayResourceMissing() {
    thrown.expect(MissingResourceException.class);
    thrown.expectMessage(
        "Can't find resource for bundle java.util.PropertyResourceBundle, key errors");
    new ResourceBundles().getGeneWayResource("errors", Locale.KOREAN);
  }

  @Test
  public void testGetOpeningHoursResource() {
    String sundayJapaneseLocalized =
        new ResourceBundles().getOpeningHoursResource("Sunday", Locale.JAPANESE);
    assertEquals("Sunday", sundayJapaneseLocalized);
  }

  @Test
  public void testGetOpeningHoursResourceMissing() {
    thrown.expect(MissingResourceException.class);
    thrown.expectMessage(
        "Can't find resource for bundle java.util.PropertyResourceBundle, key address");
    new ResourceBundles().getOpeningHoursResource("address", Locale.CANADA);
  }

  @Test
  public void testGetCongratulationsResource() {
    String happyNewYearSimplifiedChineseLocalized =
        new ResourceBundles()
            .getCongratulationsResource("happy new year", Locale.SIMPLIFIED_CHINESE);
    assertEquals("Happy New Year", happyNewYearSimplifiedChineseLocalized);
  }

  @Test
  public void testGetCongratulationsResourceMissing() {
    thrown.expect(MissingResourceException.class);
    thrown.expectMessage(
        "Can't find resource for bundle java.util.PropertyResourceBundle, key name");
    new ResourceBundles().getCongratulationsResource("name", Locale.forLanguageTag("es"));
  }
}
