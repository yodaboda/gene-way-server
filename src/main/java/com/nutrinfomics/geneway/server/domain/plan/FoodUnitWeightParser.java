package com.nutrinfomics.geneway.server.domain.plan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;

import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

public class FoodUnitWeightParser {
  public static FoodUnitWeightParser instance;

  public static final String FILE_PATH =
      System.getProperty("user.home")
          + "/Documents/gene-way-workspace/gene-way-app/references/nutrients/foodUnitWeight.csv";

  private EnumMap<FoodItemType, EnumMap<MeasurementUnit, Double>> map =
      new EnumMap<FoodItemType, EnumMap<MeasurementUnit, Double>>(FoodItemType.class);

  private FoodUnitWeightParser() {
    CSVReader reader;
    try {
      reader = new CSVReader(new FileReader(new File(FILE_PATH)));
      String[] units = reader.readNext(); // units
      String[] header = reader.readNext();
      MeasurementUnit[] measurementUnits = MeasurementUnit.parse(header, 1);
      String[] nextLine;
      while ((nextLine = reader.readNext()) != null) {
        EnumMap<MeasurementUnit, Double> measurementMap =
            new EnumMap<MeasurementUnit, Double>(MeasurementUnit.class);
        FoodItemType foodItemType = FoodItemType.valueOf(nextLine[0]);
        map.put(foodItemType, measurementMap);
        for (int i = 1; i < nextLine.length; ++i) {
          double value;
          try {
            value = Double.parseDouble(nextLine[i]);
            measurementMap.put(measurementUnits[i - 1], value);
          } catch (NumberFormatException | NullPointerException ex) {
          }
        }
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public boolean canConvertFoodMeasurementUnitToGrams(
      FoodItemType foodItemType, MeasurementUnit measurementUnit) {
    if (!map.containsKey(foodItemType)) return false;
    if (!map.get(foodItemType).containsKey(measurementUnit)) return false;
    return true;
  }

  public double convertFoodMeasurementUnitToGrams(
      FoodItemType foodItemType, MeasurementUnit measurementUnit) {
    //		System.out.println(foodItemType);
    return map.get(foodItemType).get(measurementUnit);
  }

  public Vector<MeasurementUnit> getAvailableMeasurementUnits(FoodItemType foodItemType) {
    EnumMap<MeasurementUnit, Double> measurementUnitMap = map.get(foodItemType);
    Vector<MeasurementUnit> result;
    if (measurementUnitMap == null) {
      result = new Vector<MeasurementUnit>();
    } else {
      result = new Vector<MeasurementUnit>(measurementUnitMap.keySet());
    }
    result.add(MeasurementUnit.GRAM);
    return result;
  }

  public static FoodUnitWeightParser getInstance() {
    if (instance == null) {
      synchronized (FoodUnitWeightParser.class) {
        if (instance == null) {
          instance = new FoodUnitWeightParser();
        }
      }
    }
    return instance;
  }
}
