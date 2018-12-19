package com.nutrinfomics.geneway.server.alerts.format;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

public class MeasurementUnitFormatTest {

	private ResourceBundles mockResourceBundles;
	private MeasurementUnitFormat measurementUnitFormat;

	@Before
	public void setUp() {
		mockResourceBundles = mock(ResourceBundles.class);
		measurementUnitFormat = new MeasurementUnitFormat(mockResourceBundles);
	}

	@Test
	public void format_AsExpected() {
		Locale locale = Locale.FRENCH;
		MeasurementUnit measurementUnit = MeasurementUnit.LEAF;

		String resourceString = "German Leaf";
		when(mockResourceBundles.getMeasurementResource(measurementUnit, locale)).thenReturn(resourceString);

		String formattedString = measurementUnitFormat.format(measurementUnit, locale);
		assertEquals(resourceString, formattedString);
	}

}
