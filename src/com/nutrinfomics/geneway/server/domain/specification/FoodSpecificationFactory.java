package com.nutrinfomics.geneway.server.domain.specification;

import java.util.Vector;

import com.nutrinfomics.geneway.shared.FoodCategory;
import com.nutrinfomics.geneway.shared.FoodItemType;

public class FoodSpecificationFactory {
	private static FoodSpecificationFactory instance;
	
	private FoodSpecificationFactory(){
		
	}
	
	public static FoodSpecificationFactory getInstance(){
		if(instance == null){
			synchronized (FoodSpecificationFactory.class) {
				if(instance == null){
					instance = new FoodSpecificationFactory();
				}
			}
		}
		return instance;
	}
	
	public FoodSpecification buildFoodSpecification(String item){
		FoodItemType foodItemType = parseFoodItemType(item);
		if(foodItemType != null) return new FoodItemTypeFoodSpecification(foodItemType);
		
		FoodCategory foodCategory = parseFoodCategory(item);
		if(foodCategory != null) return new FoodCategoryFoodSpecification(foodCategory);
		
		if(item.equals("animal")) return new AnimalFoodSpecification();
		
		if(item.equals("salad")) return new SaladFoodSpecification();
		
		return null;
	}

	private FoodCategory parseFoodCategory(String item) {
		try{
			return FoodCategory.valueOf(item);
		}
		catch(IllegalArgumentException e){
			return null;
		}
	}

	
	private FoodItemType parseFoodItemType(String item) {
		try{
			return FoodItemType.valueOf(item.trim().toUpperCase());
		}
		catch(IllegalArgumentException e){
			return null;
		}
	}

	public FoodSpecification buildSnackSpecification(String[] nextLine) {
		Vector<FoodSpecification> foodSpecifications = new Vector<FoodSpecification>(nextLine.length - 1);
		for(int i = 1; i < nextLine.length; ++i){
			FoodSpecification foodSpecification = buildFoodSpecification(nextLine[i]);
			if(foodSpecification != null) foodSpecifications.add(foodSpecification);
		}
		return new SnackSpecification(foodSpecifications);
	}
}
