package com.nutrinfomics.geneway.server.alert.format;

import java.text.NumberFormat;
import java.util.Locale;

import com.nutrinfomics.geneway.server.domain.plan.FoodItem;

public class FoodItemFormat {
	private static FoodItemFormat instance;
	
	public static FoodItemFormat getInstance(){
		if(instance == null){
			synchronized (FoodItemFormat.class) {
				if(instance == null){
					instance = new FoodItemFormat();
				}
			}
		}
		return instance;
	}
	
	private FoodItemFormat(){
	}
	
	public String format(FoodItem foodItem, Locale locale){
		return FoodItemTypeFormat.getInstance().format(foodItem.getFoodType(), locale) + " " +
				NumberFormat.getInstance(locale).format(foodItem.getAmount()) + " " +
				MeasurementUnitFormat.getInstance().format(foodItem.getMeasurementUnit(), locale);		
	}
}
