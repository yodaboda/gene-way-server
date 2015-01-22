package com.nutrinfomics.geneway.server.domain.specification;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.nutrinfomics.geneway.shared.FoodCategory;
import com.nutrinfomics.geneway.shared.FoodItemType;

@Entity
public class FoodCategoryFoodSpecification extends AbstractFoodSpecification{
	
	@Enumerated(EnumType.STRING)
	private FoodCategory foodCategory;

	public FoodCategoryFoodSpecification(FoodCategory foodCategory){
		this.foodCategory = foodCategory;
	}

	public FoodCategoryFoodSpecification(){
	}
	
	public FoodCategory getFoodCategory() {
		return foodCategory;
	}

	public void setFoodCategory(FoodCategory foodCategory) {
		this.foodCategory = foodCategory;
	}

	
	@Override
	public boolean qualifies(FoodItemType foodItemType) {
		return foodItemType.getFoodCategory() == foodCategory;
	}
	
	
}
