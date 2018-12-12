package com.nutrinfomics.geneway.server.domain.specification;

import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.shared.FoodItemType;

@Entity
public class SnackSpecification extends AbstractFoodSpecification {

  public static final int NO_GROUP = Integer.MIN_VALUE;

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      targetEntity = AbstractFoodSpecification.class)
  private List<FoodSpecification> foodSpecifications;

  private int groupId;

  public SnackSpecification(Vector<FoodSpecification> foodSpecifications, int group) {
    this.foodSpecifications = foodSpecifications;
    this.setGroupId(group);
  }

  public SnackSpecification() {}

  @Override
  public boolean qualifies(FoodItemType foodItemType) {
    for (FoodSpecification foodSpecification : foodSpecifications) {
      if (foodSpecification.qualifies(foodItemType)) return true;
    }
    return false;
  }

  public boolean satisfying(Vector<FoodItem> potentialItems) {
    for (FoodSpecification foodSpecification : foodSpecifications) {
      boolean satisfyingSpecifications = false;
      for (FoodItem foodItem : potentialItems) {
        if (foodSpecification.qualifies(foodItem.getFoodType())) {
          satisfyingSpecifications = true;
        }
      }
      if (!satisfyingSpecifications) return false;
    }
    return true;
  }

  public List<FoodSpecification> getFoodSpecifications() {
    return foodSpecifications;
  }

  public void setFoodSpecifications(List<FoodSpecification> foodSpecifications) {
    this.foodSpecifications = foodSpecifications;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public boolean hasGroup() {
    return groupId != NO_GROUP;
  }
}
