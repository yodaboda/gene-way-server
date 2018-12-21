package com.nutrinfomics.geneway.server.domain.plan;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.nutrinfomics.geneway.shared.FoodCategory;
import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

@Singleton
public class FoodItemUtils {

	private FoodUnitWeightParser foodUnitWeightParser;

	private EnumMap<FoodCategory, Vector<FoodItemType>> foodItemTypeByCategory;

	@Inject
	public FoodItemUtils(FoodUnitWeightParser foodUnitWeightParser) {
		this.foodUnitWeightParser = foodUnitWeightParser;
	}

	public void orderFoodItemTypeByCategory(Collection<FoodItemType> foodItemTypes,
			Map<FoodCategory, Vector<FoodItemType>> foodItemTypeByCategory) {
		for (FoodItemType foodItemType : foodItemTypes) {
			if (!foodItemTypeByCategory.containsKey(foodItemType.getFoodCategory())) {
				foodItemTypeByCategory.put(foodItemType.getFoodCategory(), new Vector<FoodItemType>());
			}
			foodItemTypeByCategory.get(foodItemType.getFoodCategory()).add(foodItemType);
		}
	}

	public synchronized Vector<FoodItemType> getFoodTypeInCategory(FoodCategory foodCategory) {
		if (foodItemTypeByCategory == null) {
			foodItemTypeByCategory = new EnumMap<>(FoodCategory.class);
			orderFoodItemTypeByCategory(Arrays.asList(FoodItemType.values()), foodItemTypeByCategory);
		}
		return foodItemTypeByCategory.get(foodCategory);
	}

	public void orderFoodItemByCategory(Collection<FoodItem> foodItems,
			Map<FoodCategory, Vector<FoodItem>> foodItemByCategory) {
		for (FoodItem foodItem : foodItems) {
			if (!foodItemByCategory.containsKey(foodItem.getFoodType().getFoodCategory())) {
				foodItemByCategory.put(foodItem.getFoodType().getFoodCategory(), new Vector<FoodItem>());
			}
			foodItemByCategory.get(foodItem.getFoodType().getFoodCategory()).add(foodItem);
		}
	}

	public FoodItem convertToGrams(FoodItem foodItem) {
		MeasurementUnit measurementUnit = foodItem.getMeasurementUnit();
		double amount = foodItem.getAmount();
		double amountInGrams = amount;
		if (measurementUnit != MeasurementUnit.GRAM) {
			double conversionRatio = foodUnitWeightParser.convertFoodMeasurementUnitToGrams(foodItem.getFoodType(),
					measurementUnit);
			amountInGrams = amount * conversionRatio;
		}
		return new FoodItem(amountInGrams, MeasurementUnit.GRAM, foodItem.getFoodType());
	}
}
