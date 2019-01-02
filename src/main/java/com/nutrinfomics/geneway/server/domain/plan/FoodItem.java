package com.nutrinfomics.geneway.server.domain.plan;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

@Entity
public class FoodItem extends EntityBase implements Serializable {
  private double amount;

  @Enumerated(EnumType.STRING)
  private MeasurementUnit measurementUnit;

  @Enumerated(EnumType.STRING)
  private FoodItemType foodType;

  @Embedded private ArbitraryCycle cycle = new ArbitraryCycle();

  public FoodItem() {}

  public FoodItem(String foodType) {
    this(FoodItemType.valueOf(foodType.toUpperCase()));
  }

  public FoodItem(FoodItem foodItem) {
    this(
        foodItem.getAmount(),
        foodItem.getMeasurementUnit(),
        foodItem.getFoodType(),
        new ArbitraryCycle(foodItem.getCycle()));
  }

  public FoodItem(FoodItemType foodType) {
    this(0, MeasurementUnit.GRAM, foodType);
  }

  public FoodItem(double amount, MeasurementUnit measurementUnit, FoodItemType foodType) {
    this(amount, measurementUnit, foodType, new ArbitraryCycle(7));
  }

  public FoodItem(
      double amount, MeasurementUnit measurementUnit, FoodItemType foodType, ArbitraryCycle cycle) {
    setAmount(amount);
    setMeasurementUnit(measurementUnit);
    setFoodType(foodType);
    setCycle(cycle);
  }

  public FoodItem(String[] data) {
    setFoodType(FoodItemType.valueOf(data[0]));
    setMeasurementUnit(MeasurementUnit.valueOf(data[1]));
    setAmount(Double.parseDouble(data[2]));
    getCycle().setCycleLength(Integer.parseInt(data[3]));
  }

  public double getAmount() {
    return amount;
  }

  public double getWeeklyNormalizedAmount() {
    return amount * getCycle().getCycleLength() / 7.0;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public MeasurementUnit getMeasurementUnit() {
    return measurementUnit;
  }

  public void setMeasurementUnit(MeasurementUnit measurementUnit) {
    this.measurementUnit = measurementUnit;
  }

  public FoodItemType getFoodType() {
    return foodType;
  }

  public void setFoodType(FoodItemType foodType) {
    this.foodType = foodType;
  }

  @Override
  public String toString() {
    return foodType + " " + measurementUnit + " " + amount + " " + cycle.getCycleLength();
  }

  public String[] toStrings() {
    return new String[] {
      foodType + "", measurementUnit + "", amount + "", cycle.getCycleLength() + ""
    };
  }

  public ArbitraryCycle getCycle() {
    return cycle;
  }

  public void setCycle(ArbitraryCycle cycle) {
    this.cycle = cycle;
  }
}
