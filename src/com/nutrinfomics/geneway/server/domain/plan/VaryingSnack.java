package com.nutrinfomics.geneway.server.domain.plan;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.shared.FoodCategory;
import com.nutrinfomics.geneway.shared.FoodItemType;

@Entity
@Access(AccessType.FIELD)
public class VaryingSnack extends Snack implements WeeklyBehaving{

	
	@OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Snack> weeklySnacks = new ArrayList<>(7);

	@Transient
 	private BitSet eaten = new BitSet(7);
	
	@Transient
	private List<FoodItem> todaysFoodItems = new ArrayList<>();
	
	@Transient
	private List<FoodItem> animalFoodItems = new ArrayList<>();

	@Transient
	private List<FoodItem> salad = new ArrayList<>();

	@Transient
	private List<FoodItem> otherFoodItems = new ArrayList<>();

	@Transient
	private FoodItem animalFoodItem;
	
	@Transient
	private List<FoodItem> pickedOtherFoodItems = new ArrayList<>();
	
//	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@Transient
	private WeeklyCycle weeklyCycle = WeeklyCycle.getInstance();

	private static Random randomGenerator = new Random();

	public void init(){
		for(int i = 0; i < 7; ++i){
			Snack snack = getNextCalculatedSnack();
			weeklySnacks.add(snack);
			eaten.set(i,false);
			nextDay();
		}
	}
	

    @Access(AccessType.PROPERTY) // Store the property instead
    @Column(name = "eaten")
    byte[] getEatenInDbRepresentation() {
    	return eaten.toByteArray();
    }

    void setEatenInDbRepresentation(byte[] data) {
        eaten = BitSet.valueOf(data);
    }
	
	private Snack getNextCalculatedSnack(){
		prepare();
		
		Snack snack = new Snack(copy(todaysFoodItems));
		snack.setSnackProperty(getSnackProperty());
		snack.setTime(getTime());
		return snack;
	}
	
	private List<FoodItem> copy(List<FoodItem> foodItems) {
		List<FoodItem> foodItemsCopy = new ArrayList<>(foodItems.size());
		for(FoodItem foodItem : foodItems){
			FoodItem foodItemCopy = new FoodItem(foodItem);
			foodItemsCopy.add(foodItemCopy);
		}
		return foodItemsCopy;
	}

	public Snack getTodaysSnack(){
		try{
			List<Integer> indices = new ArrayList<>(7);
			for(int i = 0; i < 7; ++i) indices.add(i);
			Collections.shuffle(indices);
			
			for(int i = 0; i <indices.size(); ++i){
				if(!eaten.get(indices.get(i))){
					eaten.set(indices.get(i), true);
					return weeklySnacks.get(indices.get(i));
				}
			}
		
			//otherwise, all snacks already eaten - end of week - reset
			for(int i = 0; i < 7; ++i) eaten.set(i, false);

			eaten.set(indices.get(0), true);
			return weeklySnacks.get(indices.get(0));
		}
		finally{
			merge();
		}
	}
	
	private void clear(){
		todaysFoodItems.clear();
		animalFoodItems.clear();
		salad.clear();
		otherFoodItems.clear();
		pickedOtherFoodItems.clear();
		animalFoodItem = null;
	}
	
	private void prepare() {
		clear();
		initFoodItemClassification();

		initAnimalFoodItemWidget();
		initSaladFoodItemWidgets();
		initOtherFoodItemWidgets();
	}
	
	private void initOtherFoodItemWidgets() {
		for(FoodItem foodItem : otherFoodItems){
			ArbitraryCycle arbitraryCycle = foodItem.getCycle();
			if(arbitraryCycle.getCycleLength() >= 1 && 
					(arbitraryCycle.getRemainingLength() == weeklyCycle.getRemainingLength() ||
					randomGenerator.nextBoolean() )){
				pickedOtherFoodItems.add(foodItem);
				todaysFoodItems.add(foodItem);
			}
		}
		
	}

	private void initSaladFoodItemWidgets() {
		for(FoodItem foodItem : salad){
			todaysFoodItems.add(foodItem);
		}
	}

	private void initAnimalFoodItemWidget() {
		for(FoodItem foodItem : animalFoodItems){
			if(foodItem.getCycle().getRemainingLength() >= 1){
				todaysFoodItems.add(foodItem);
				animalFoodItem = foodItem;
				break;
			}
		}
	}

	private void initFoodItemClassification() {
		for(FoodItem foodItem : foodItems){
			
			FoodItemType foodType = foodItem.getFoodType();
			FoodCategory foodCategory = foodType.getFoodCategory();
			
			if(foodCategory == FoodCategory.VEGETABLE_FRUIT && foodType != FoodItemType.ZUCCHINI && foodType != FoodItemType.SQUASH_SUMMER &&
					foodType != FoodItemType.PUMPKIN){
				salad.add(foodItem);
			}
			else if(foodCategory == FoodCategory.VEGETABLE &&
					(foodType == FoodItemType.PARSLEY || foodType == FoodItemType.LETTUCE || foodType == FoodItemType.ARUGULA ||
					foodType == FoodItemType.SPEARMINT || foodType == FoodItemType.ONION_YOUNG_GREEN || foodType == FoodItemType.CABBAGE ||
					foodType == FoodItemType.BROCCOLI)){
				salad.add(foodItem);
			}
			else if(foodType == FoodItemType.LEMON_JUICE){
				salad.add(foodItem);
			}
			else if(foodType == FoodItemType.CARROT){
				salad.add(foodItem);
			}
			else if(foodType == FoodItemType.OLIVE_OIL || foodType == FoodItemType.COCONUT_OIL){
				salad.add(foodItem);
			}
			else if(foodType == FoodItemType.AVOCADO || foodType == FoodItemType.OLIVE || foodType == FoodItemType.CHEESE_FETA){
				salad.add(foodItem);
			}
			else if(foodCategory == FoodCategory.MEAT || foodCategory == FoodCategory.FISH || foodCategory == FoodCategory.SEAFOOD){
				animalFoodItems.add(foodItem);
			}
			else{
				otherFoodItems.add(foodItem);
			}
		}
	}

	@Override
	public void nextDay(){
		animalFoodItem.getCycle().advanceBySingleUnit();

		for(FoodItem foodItem : pickedOtherFoodItems){
			foodItem.getCycle().advanceBySingleUnit();
		}
	}
	
	@Override
	public void weeklyReset(){
		prepare();
		
		for(FoodItem foodItem : animalFoodItems)
			foodItem.getCycle().reset();
		
		for(FoodItem otherFoodItem : otherFoodItems)
			otherFoodItem.getCycle().reset();
	}
}
