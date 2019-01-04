package com.nutrinfomics.geneway.server.alerts.format;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.SnackProperty;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class SnackPropertyFormatTest {

  private ResourceBundles mockResourceBundles;

  private SnackPropertyFormat snackPropertyFormat;

  @Before
  public void setUp() {
    mockResourceBundles = mock(ResourceBundles.class);
    snackPropertyFormat = new SnackPropertyFormat(mockResourceBundles);
  }

  @Test
  public void format_AsExpected() {
    Locale locale = Locale.JAPANESE;
    SnackProperty snackProperty = SnackProperty.ENERGY;
    String snackPropertyString = snackProperty.toString();
    String formatSnackProperty = "Rest in Korean";
    when(mockResourceBundles.getMiscResource(snackPropertyString, locale))
        .thenReturn(formatSnackProperty);
    String formattedSnackProperty = snackPropertyFormat.format(snackProperty, locale);

    assertEquals(formatSnackProperty, formattedSnackProperty);
  }
}
