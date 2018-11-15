package com.nutrinfomics.geneway.server.alert.format;

import java.util.Locale;

import com.nutrinfomics.geneway.server.ResourceBundles;
import com.nutrinfomics.geneway.shared.FoodItemType;

public class FoodItemTypeFormat {

	private static FoodItemTypeFormat instance;
	
	public static FoodItemTypeFormat getInstance(){
		if(instance == null){
			synchronized (FoodItemTypeFormat.class) {
				if(instance == null){
					instance = new FoodItemTypeFormat();
				}
			}
		}
		return instance;
	}
	
	private FoodItemTypeFormat(){
	}
	
	
	public String format(FoodItemType foodItemType, Locale locale){
		return ResourceBundles.getFoodItemResource(foodItemType, locale);
//		return ResourceBundle.getBundle("FoodItemTypeBundle", locale).getString(foodItemType.toString());		
	}
}
