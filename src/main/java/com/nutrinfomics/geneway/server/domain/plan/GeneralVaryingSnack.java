package com.nutrinfomics.geneway.server.domain.plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class GeneralVaryingSnack extends Snack {

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Snack> snacks = new ArrayList<Snack>();

  @Override
  public void add(Snack snack) {
    snacks.add(snack);
  }

  public List<Snack> getSnacks() {
    return snacks;
  }

  public Snack pickTodaysSnack() {
    List<Integer> indices = new ArrayList<>(snacks.size());
    for (int i = 0; i < snacks.size(); ++i) indices.add(i);
    Collections.shuffle(indices);

    for (int i = 0; i < indices.size(); ++i) {
      Snack snack = snacks.get(indices.get(i));
      ArbitraryCycle cycle = snack.getFoodItems().get(0).getCycle();
      if (cycle.getCycleLength() >= 1) {
        cycle.advanceBySingleUnit();
        return snack;
      }
    }
    return null;
  }

  @Override
  public void add(FoodItem foodItem) {
    for (Snack snack : getSnacks()) {
      snack.add(foodItem);
    }
  }

  public void reset() {
    for (Snack snack : snacks) {
      snack.getFoodItems().get(0).getCycle().reset();
    }
  }

  @Override
  public String getSummary() {
    return snacks.get(0).getSummary();
  }
}
