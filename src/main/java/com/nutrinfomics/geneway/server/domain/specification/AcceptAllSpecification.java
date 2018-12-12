package com.nutrinfomics.geneway.server.domain.specification;

import javax.persistence.Entity;

import com.nutrinfomics.geneway.shared.FoodItemType;

@Entity
public class AcceptAllSpecification extends AbstractFoodSpecification {

  @Override
  public boolean qualifies(FoodItemType foodItemType) {
    return true; // accept all
  }
}
