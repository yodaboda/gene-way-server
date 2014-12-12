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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.shared.SnackProperty;

@Entity
public class Snack extends EntityBase implements Serializable{
	
	@OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
	private List<FoodItem> foodItems;
	
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

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackProxy#add(com.nutrinfomics.geneway.server.domain.plan.FoodItem)
	 */
	public void add(FoodItem foodItem){
		foodItems.add(foodItem);
	}
	
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackProxy#getFoodItems()
	 */
	public List<FoodItem> getFoodItems() {
		return foodItems;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackProxy#add(com.nutrinfomics.geneway.server.domain.plan.Snack)
	 */
	public void add(Snack snack) {
		foodItems.addAll(snack.getFoodItems());
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackProxy#add(java.util.Vector)
	 */
	public void add(Vector<FoodItem> foodItems) {
		this.foodItems.addAll(foodItems);
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackProxy#getSnackProperty()
	 */
	public SnackProperty getSnackProperty() {
		return snackProperty;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackProxy#setSnackProperty(com.nutrinfomics.geneway.server.domain.plan.Snack.SnackProperty)
	 */
	public void setSnackProperty(SnackProperty snackProperty) {
		this.snackProperty = snackProperty;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackProxy#getTime()
	 */
	public Date getTime() {
		return time;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackProxy#setTime(java.util.Date)
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	public boolean isConsumed() {
		// TODO Auto-generated method stub
		return false;
	}

}
