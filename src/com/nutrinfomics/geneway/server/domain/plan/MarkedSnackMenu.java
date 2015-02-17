package com.nutrinfomics.geneway.server.domain.plan;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.nutrinfomics.geneway.server.alert.Alerts;
import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.specification.AbstractFoodSpecification;
import com.nutrinfomics.geneway.server.domain.specification.AcceptAllSpecification;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;

@Entity
public class MarkedSnackMenu extends EntityBase {

	//this is a set, because when using a list there were many duplicates
	@OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
	private Set<MarkedSnack> markedSnacks;

	private String date;
	
	@OneToOne(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
	private MarkedSnack currentSnack;
	
	public MarkedSnackMenu(){
		
	}
	
	public MarkedSnackMenu(String date, List<Snack> snacks){
		this.setDate(date);
		setSnacks(snacks);
	}

	public MarkedSnack calcCurrentSnack(SnackOrderSpecification snackOrderSpecification){
		if(currentSnack != null && ! currentSnack.isMarked()) return currentSnack;
		
		if(getMarkedSnacks().isEmpty()){
			setCurrentSnack(null);
			return null;
		}
		
		List<AbstractFoodSpecification> foodOrderSpecification = snackOrderSpecification.getFoodOrderSpecification();
		int i = foodOrderSpecification.size() - getMarkedSnacks().size();
		assert i >= 0;
		AbstractFoodSpecification currentFoodSpecification = foodOrderSpecification.get(i);
		
		MarkedSnack foundSnack = null;
		for(MarkedSnack markedSnack : getMarkedSnacks()){
			if(markedSnack.isMarked()) continue;
			if(isSnackSpecificationFitting(currentFoodSpecification, markedSnack)){
				boolean found = true;
				for(int j = i + 1; j < foodOrderSpecification.size(); ++j){
					AbstractFoodSpecification futureFoodSpecification = foodOrderSpecification.get(j);
					if(futureFoodSpecification instanceof AcceptAllSpecification) continue;
					else if(isSnackSpecificationFitting(futureFoodSpecification, markedSnack)){
						found = false;
						break;
					}
				}
				if(found){
					foundSnack = markedSnack;
					break;
				}
			}
		}
		if(foundSnack != null) getMarkedSnacks().remove(foundSnack);
		setCurrentSnack(foundSnack);
		return foundSnack;
	}
	
	
	private boolean isSnackSpecificationFitting(
			AbstractFoodSpecification abstractFoodSpecification,
			MarkedSnack markedSnack) {
		for(FoodItem foodItem : markedSnack.getSnack().getFoodItems()){
			if(abstractFoodSpecification.qualifies(foodItem.getFoodType())){
				return true;
			}
		}
		return false;
	}

	public MarkedSnack getCurrentSnack() {
		return currentSnack;
	}

	public void setCurrentSnack(MarkedSnack currentSnack) {
		this.currentSnack = currentSnack;
	}

	public Set<MarkedSnack> getMarkedSnacks() {
		return markedSnacks;
	}

	public void setMarkedSnacks(Set<MarkedSnack> markedSnacks) {
		this.markedSnacks = markedSnacks;
	}

	public void setSnacks(List<Snack> snacks) {
		this.markedSnacks = new HashSet<MarkedSnack>(snacks.size());
		for(Snack snack : snacks){

			this.markedSnacks.add(new MarkedSnack(snack));
		}
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
