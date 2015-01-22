package com.nutrinfomics.geneway.server.domain.specification;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.nutrinfomics.geneway.shared.FoodItemType;

@Entity
public class FoodItemTypeFoodSpecification extends AbstractFoodSpecification {

	@Enumerated(EnumType.STRING)
	private FoodItemType foodItemType;

	public FoodItemTypeFoodSpecification(FoodItemType foodItemType) {
		this.foodItemType = foodItemType;
	}
	
	public FoodItemTypeFoodSpecification() {
	}
	
	@Override
	public boolean qualifies(FoodItemType foodItemType) {
		return this.foodItemType == foodItemType;
	}

	public FoodItemType getFoodItemType() {
		return foodItemType;
	}

	public void setFoodItemType(FoodItemType foodItemType) {
		this.foodItemType = foodItemType;
	}

}
