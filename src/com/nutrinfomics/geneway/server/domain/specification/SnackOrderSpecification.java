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
	private List<AbstractFoodSpecification> snackOrderSpecification;

	public SnackOrderSpecification(){
		this(10);
	}
	
	public SnackOrderSpecification(int size){
		snackOrderSpecification = new ArrayList<>(size);		
	}
	
	public List<AbstractFoodSpecification> getSnackOrderSpecification() {
		return snackOrderSpecification;
	}

	public void setSnackOrderSpecification(
			List<AbstractFoodSpecification> snackOrderSpecification) {
		this.snackOrderSpecification = snackOrderSpecification;
	}
	
}
