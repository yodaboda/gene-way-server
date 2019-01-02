package com.nutrinfomics.geneway.server.domain.plan;

import java.util.List;
import java.util.Vector;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class WeeklyCycle extends ArbitraryCycle {
  private static final int CYCLE_LENGTH = 7;

  @Transient private List<WeeklyBehaving> weeklyBehavings = new Vector<>();

  private static WeeklyCycle instance;

  public static WeeklyCycle getInstance() {
    if (instance == null) {
      // try to retrieve the only weekly cycle instance
      //			EntityManager entityManager = HibernateUtil.getInstance().getEntityManager();
      //			TypedQuery<WeeklyCycle> query = entityManager.createQuery("SELECT s FROM WeeklyCycle",
      // WeeklyCycle.class);
      //			try{
      //				instance = query.getSingleResult();
      //			}
      //			catch(Exception e){
      //				instance = new WeeklyCycle();
      //			}
      instance = new WeeklyCycle();
    }

    return instance;
  }

  private WeeklyCycle() {
    super(CYCLE_LENGTH);
  }

  @Override
  public void advanceBySingleUnit() {
    super.advanceBySingleUnit();
    if (getRemainingLength() < 1) {
      setRemainingLength(CYCLE_LENGTH);
      ; // restart
      for (WeeklyBehaving weeklyBehaving : weeklyBehavings) weeklyBehaving.weeklyReset();
    }
    for (WeeklyBehaving weeklyBehaving : weeklyBehavings) weeklyBehaving.nextDay();
  }

  public void addWeeklyBehaving(WeeklyBehaving weeklyBehaving) {
    weeklyBehavings.add(weeklyBehaving);
  }
}
