package com.nutrinfomics.geneway.server.alert.format;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.shared.constants.ResourceBundles;

public class SnackFormat {
	private static SnackFormat instance;
	
	public static SnackFormat getInstance(){
		if(instance == null){
			synchronized (SnackFormat.class) {
				if(instance == null){
					instance = new SnackFormat();
				}
			}
		}
		return instance;
	}
	
	private SnackFormat(){
	}
	
	public String format(Snack snack, Locale locale){
		Collection<FoodItem> foodItems = snack.getFoodItems();
		String s = "";
		for(FoodItem foodItem : foodItems){
			s += FoodItemFormat.getInstance().format(foodItem, locale) + " + ";			
		}
		return s.isEmpty() ? s : s.substring(0, s.length() - 2); // string ends with "+ "
	}

	public String formatMeatSnack(Snack snack, Locale locale){
		Collection<FoodItem> foodItems = snack.getFoodItems();
		String s = "";
		for(FoodItem foodItem : foodItems){
			s += FoodItemFormat.getInstance().format(new FoodItem(foodItem.getAmount(), foodItem.getMeasurementUnit(), foodItem.getFoodType()), 
					locale) +
					" - " + NumberFormat.getIntegerInstance(locale).format(foodItem.getCycle().getCycleLength()) + " " +
					ResourceBundles.getMiscBundleResource("days", locale) + System.getProperty("line.separator");			
		}
		return s.isEmpty() ? s : s.substring(0, s.length() - 1); // string ends with "\n"
	}

}
