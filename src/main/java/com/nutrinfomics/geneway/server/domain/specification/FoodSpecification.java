package com.nutrinfomics.geneway.server.domain.specification;

import com.nutrinfomics.geneway.shared.FoodItemType;

public interface FoodSpecification {
  public boolean qualifies(FoodItemType foodItemType);
}
