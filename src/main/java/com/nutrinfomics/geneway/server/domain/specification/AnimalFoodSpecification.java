package com.nutrinfomics.geneway.server.domain.specification;

import javax.persistence.Entity;

import com.nutrinfomics.geneway.shared.FoodCategory;
import com.nutrinfomics.geneway.shared.FoodItemType;

@Entity
public class AnimalFoodSpecification extends AbstractFoodSpecification {

  @Override
  public boolean qualifies(FoodItemType foodItemType) {
    FoodCategory foodCategory = foodItemType.getFoodCategory();
    return foodCategory == FoodCategory.FISH
        || foodCategory == FoodCategory.MEAT
        || foodCategory == FoodCategory.SEAFOOD;
  }
}
