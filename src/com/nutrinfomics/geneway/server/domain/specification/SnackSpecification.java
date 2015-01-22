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

	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, targetEntity=AbstractFoodSpecification.class)
	private List<FoodSpecification> foodSpecifications;

	public SnackSpecification(Vector<FoodSpecification> foodSpecifications){
		this.foodSpecifications = foodSpecifications;
	}

	public SnackSpecification(){
	}
	
	@Override
	public boolean qualifies(FoodItemType foodItemType) {
		for(FoodSpecification foodSpecification : foodSpecifications){
			if(foodSpecification.qualifies(foodItemType)) return true;
		}
		return false;
	}

	public boolean satisfying(Vector<FoodItem> potentialItems) {
		for(FoodSpecification foodSpecification : foodSpecifications){
			boolean satisfyingSpecifications = false;
			for(FoodItem foodItem : potentialItems){
				if(foodSpecification.qualifies(foodItem.getFoodType())){
					satisfyingSpecifications = true;
				}
			}
			if(!satisfyingSpecifications) return false;
		}
		return true;
	}

	public List<FoodSpecification> getFoodSpecifications() {
		return foodSpecifications;
	}

	public void setFoodSpecifications(List<FoodSpecification> foodSpecifications) {
		this.foodSpecifications = foodSpecifications;
	}

}
