package com.nutrinfomics.geneway.server.domain.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class SnackOrderSpecification extends EntityBase{
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<AbstractFoodSpecification> foodOrderSpecification;

	public SnackOrderSpecification(){
		this(10);
	}
	
	public SnackOrderSpecification(int size){
		foodOrderSpecification = new ArrayList<>(size);		
	}
	
	public List<AbstractFoodSpecification> getFoodOrderSpecification() {
		return foodOrderSpecification;
	}

	public void setFoodOrderSpecification(
			List<AbstractFoodSpecification> foodOrderSpecification) {
		this.foodOrderSpecification = foodOrderSpecification;
	}
	
}
