package com.nutrinfomics.geneway.server.domain.plan;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Vector;

import com.nutrinfomics.geneway.server.domain.ModelObject;
import com.nutrinfomics.geneway.shared.FoodCategory;
import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

public class FoodItem extends ModelObject implements Serializable{
	private double amount;
	private MeasurementUnit measurementUnit;
	private FoodItemType foodType;
	private int weeklyDays;
	private int usedWeeklyDays = 0;
	private static EnumMap<FoodCategory, Vector<FoodItemType>> foodItemTypeByCategory;
	
	public FoodItem(){
		
	}
	
	public FoodItem(String foodType){
		this(FoodItemType.valueOf(foodType.toUpperCase()));
	}
	
	public FoodItem(FoodItem foodItem){
		this(foodItem.getAmount(), foodItem.getMeasurementUnit(),
				foodItem.getFoodType(), foodItem.getWeeklyDays());
	}
	
	public FoodItem(FoodItemType foodType){
		this(0, MeasurementUnit.GRAM, foodType);
	}

	public FoodItem(double amount, MeasurementUnit measurementUnit, 
			FoodItemType foodType){
		this(amount, measurementUnit, foodType, 7);
	}

	
	public FoodItem(double amount, MeasurementUnit measurementUnit, 
					FoodItemType foodType, int weeklyDays){
		setAmount(amount);
		setMeasurementUnit(measurementUnit);
		setFoodType(foodType);
		setWeeklyDays(weeklyDays);
	}

	public FoodItem(String[] data) {
		setFoodType(FoodItemType.valueOf(data[0]));
		setMeasurementUnit(MeasurementUnit.valueOf(data[1]));
		setAmount(Double.parseDouble(data[2]));
		setWeeklyDays(Integer.parseInt(data[3]));
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#getAmount()
	 */
	public double getAmount() {
		return amount;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#getWeeklyNormalizedAmount()
	 */
	public double getWeeklyNormalizedAmount(){
		return amount * getWeeklyDays() / 7.0;
	}
	
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#setAmount(double)
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#getMeasurementUnit()
	 */
	public MeasurementUnit getMeasurementUnit() {
		return measurementUnit;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#setMeasurementUnit(com.nutrinfomics.geneway.server.domain.plan.MeasurementUnit)
	 */
	public void setMeasurementUnit(MeasurementUnit measurementUnit) {
		this.measurementUnit = measurementUnit;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#getFoodType()
	 */
	public FoodItemType getFoodType() {
		return foodType;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#setFoodType(com.nutrinfomics.geneway.server.domain.plan.FoodItemType)
	 */
	public void setFoodType(FoodItemType foodType) {
		this.foodType = foodType;
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#toString()
	 */
	@Override
	public String toString(){
		return foodType + " " + measurementUnit + " " + amount + " " + weeklyDays;
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#toStrings()
	 */
	public String[] toStrings(){
		return new String[]{foodType + "", measurementUnit + "", amount + "",
							weeklyDays + ""};
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#getWeeklyDays()
	 */
	public int getWeeklyDays() {
		return weeklyDays;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#setWeeklyDays(int)
	 */
	public void setWeeklyDays(int weeklyDays) {
		this.weeklyDays = weeklyDays;
	}
	//should be less than weeklyDays
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#getUsedWeeklyDays()
	 */
	public int getUsedWeeklyDays() {
		return usedWeeklyDays;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#advanceUsedWeeklyDaysBySingleUnit()
	 */
	public void advanceUsedWeeklyDaysBySingleUnit() {
		this.usedWeeklyDays++;
	}
	
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#getDaysYetToBeUsed()
	 */
	public int getDaysYetToBeUsed(){
		return weeklyDays - usedWeeklyDays;
	}
	
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#isToBeUsed()
	 */
	public boolean isToBeUsed(){
		return getDaysYetToBeUsed() > 0;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.FoodItemProxy#resetWeekly()
	 */
	public void resetWeekly() {
		usedWeeklyDays = 0;
	}

	public static Vector<FoodItemType> getFoodTypeInCategory(FoodCategory foodCategory){
		if(foodItemTypeByCategory == null){
			synchronized(FoodItemType.class){
				if(foodItemTypeByCategory == null){
					foodItemTypeByCategory = new EnumMap<FoodCategory, Vector<FoodItemType>>(FoodCategory.class);
					orderFoodItemTypeByCategory(Arrays.asList(FoodItemType.values()), foodItemTypeByCategory);									
				}
			}
		}
		return foodItemTypeByCategory.get(foodCategory);
	}

	public static void orderFoodItemTypeByCategory(Collection<FoodItemType> foodItemTypes, EnumMap<FoodCategory, Vector<FoodItemType>> foodItemTypeByCategory) {
		for(FoodItemType foodItemType : foodItemTypes){
			if(! foodItemTypeByCategory.containsKey(foodItemType.getFoodCategory())){
				foodItemTypeByCategory.put(foodItemType.getFoodCategory(), new Vector<FoodItemType>());
			}
			foodItemTypeByCategory.get(foodItemType.getFoodCategory()).add(foodItemType);;
		}
	}

	public static void orderFoodItemByCategory(Collection<FoodItem> foodItems, EnumMap<FoodCategory, Vector<FoodItem>> foodItemByCategory) {
		for(FoodItem foodItem : foodItems){
			if(! foodItemByCategory.containsKey(foodItem.getFoodType().getFoodCategory())){
				foodItemByCategory.put(foodItem.getFoodType().getFoodCategory(), new Vector<FoodItem>());
			}
			foodItemByCategory.get(foodItem.getFoodType().getFoodCategory()).add(foodItem);;
		}
	}

		public FoodItem convertToGrams(){
			MeasurementUnit measurementUnit = getMeasurementUnit();
			double amount = getAmount();
			double amountInGrams = amount;
			if(measurementUnit != MeasurementUnit.GRAM){
				double conversionRatio = FoodUnitWeightParser.getInstance().convertFoodMeasurementUnitToGrams(getFoodType(), measurementUnit);
				amountInGrams = amount * conversionRatio;
			}
			return new FoodItem(amountInGrams, MeasurementUnit.GRAM, getFoodType());
	}
}
