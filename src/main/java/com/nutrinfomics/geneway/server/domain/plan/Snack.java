package com.nutrinfomics.geneway.server.domain.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.shared.FoodCategory;
import com.nutrinfomics.geneway.shared.SnackProperty;

@Entity
public class Snack extends EntityBase implements Serializable{
	
	@OneToMany(fetch=FetchType.LAZY, cascade = {CascadeType.ALL})
	protected List<FoodItem> foodItems;
	
	@Enumerated(EnumType.STRING)
	private SnackProperty snackProperty = null;
	
	@Temporal(TemporalType.TIME)
	private Date time = null;
	
	public Snack(FoodItem[] foodItems) {
		this(Arrays.asList(foodItems));
	}

	public Snack(FoodItem foodItem){
		this();
		add(foodItem);
	}
	
	public Snack(){
		foodItems = new ArrayList<FoodItem>();
	}
	
	public Snack(List<FoodItem> foodItems) {
		this.foodItems = foodItems;
	}

	public void add(FoodItem foodItem){
		foodItems.add(foodItem);
	}
	
	public List<FoodItem> getFoodItems() {
		return foodItems;
	}

	public void add(Snack snack) {
		foodItems.addAll(snack.getFoodItems());
	}

	public void add(Collection<FoodItem> foodItems) {
		this.foodItems.addAll(foodItems);
	}

	public SnackProperty getSnackProperty() {
		return snackProperty;
	}

	public void setSnackProperty(SnackProperty snackProperty) {
		this.snackProperty = snackProperty;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getSummary() {
		if(snackProperty == SnackProperty.REST) return SnackProperty.REST.toString();
		if(snackProperty == SnackProperty.ENERGY){
			for(FoodItem foodItem : getFoodItems()){
				if(foodItem.getFoodType().getFoodCategory() == FoodCategory.FRUIT) return foodItem.getFoodType().toString();
			}
		}
		return getFoodItems().get(0).getFoodType().toString();
	}
}
