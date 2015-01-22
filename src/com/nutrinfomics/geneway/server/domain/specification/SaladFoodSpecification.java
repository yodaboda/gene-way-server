package com.nutrinfomics.geneway.server.domain.specification;

import com.nutrinfomics.geneway.shared.FoodCategory;
import com.nutrinfomics.geneway.shared.FoodItemType;

public class SaladFoodSpecification extends AbstractFoodSpecification {

	@Override
	public boolean qualifies(FoodItemType foodItemType) {
		FoodCategory foodCategory = foodItemType.getFoodCategory();
		return foodCategory == FoodCategory.VEGETABLE_FRUIT ||
				foodCategory == FoodCategory.VEGETABLE ||
				foodCategory == FoodCategory.ROOT ||
				foodItemType == FoodItemType.ZUCCHINI ||
				foodItemType == FoodItemType.SQUASH_SUMMER ||
				foodItemType == FoodItemType.PUMPKIN ||
				foodItemType == FoodItemType.OLIVE ||
				foodItemType == FoodItemType.AVOCADO ||
				foodItemType == FoodItemType.LEMON_JUICE ||
				foodItemType == FoodItemType.OLIVE_OIL ||
				foodItemType == FoodItemType.COCONUT_OIL ||
				foodItemType == FoodItemType.RICE ||
				foodItemType == FoodItemType.BEAN_SNAP_YELLOW;
	}

}
