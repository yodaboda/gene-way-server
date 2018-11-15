package com.nutrinfomics.geneway.server.alert.format;

import java.util.Locale;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

public class MeasurementUnitFormat {
	private static MeasurementUnitFormat instance;
	
	public static MeasurementUnitFormat getInstance(){
		if(instance == null){
			synchronized (MeasurementUnitFormat.class) {
				if(instance == null){
					instance = new MeasurementUnitFormat();
				}
			}
		}
		return instance;
	}
	
	private MeasurementUnitFormat(){
	}

	public String format(MeasurementUnit measurementUnit, Locale locale){
		return ResourceBundles.getMeasurementResource(measurementUnit, locale);
//		return ResourceBundle.getBundle("MeasurementBundle", locale).getString(measurementUnit.toString());		
	}
	
}
