package com.nutrinfomics.geneway.server.requestfactory.request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import sk.nociar.jpacloner.JpaCloner;
import sk.nociar.jpacloner.PropertyFilter;

import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.server.domain.plan.GeneralVaryingSnack;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackMenu;
import com.nutrinfomics.geneway.server.domain.specification.AbstractFoodSpecification;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;
import com.nutrinfomics.geneway.shared.FoodItemType;

public class PlanService {
  private Provider<EntityManager> entityManager;
  private HibernateUtil hibernateUtil;

  @Inject
  public PlanService(Provider<EntityManager> entityManager, HibernateUtil hibernateUtil) {
    this.entityManager = entityManager;
    this.hibernateUtil = hibernateUtil;
  }

  @Transactional
  public void setDemo(Session session) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager);
    //		sessionDb.getCustomer().getDevice().setCode("demo");

    //		PersonalDetails personalDetails = new PersonalDetails();
    //		personalDetails.setGender(Gender.FEMALE);
    //		personalDetails.setBirthday(new SimpleDate(28, 7, 1975));
    //		personalDetails.setCustomer(sessionDb.getCustomer());
    //		sessionDb.getCustomer().setPersonalDetails(personalDetails);

    Customer demoCustomer = entityManager.get().find(Customer.class, new Long(4));

    Plan plan = demoCustomer.getPlan();

    PropertyFilter myFilter =
        new PropertyFilter() {
          public boolean test(Object entity, String property) {
            return !("id".equals(property) || "version".equals(property));
          }
        };
    Plan copyPlan = JpaCloner.clone(plan, myFilter, "*.*.*.*.*");

    sessionDb.getCustomer().setPlan(copyPlan);

    //		PersonalDetails personalDetails = new PersonalDetails();
    //		personalDetails.setGender(Gender.FEMALE);
    //		personalDetails.setNickName("demo");
    //		personalDetails.setBirthday(LocalDate.of(1975, 7, 28));
    //		entityManager.get().persist(personalDetails);

    //		sessionDb.getCustomer().setPersonalDetails(personalDetails);
    //		personalDetails.setCustomer(sessionDb.getCustomer());

    //		entityManager.get().merge(sessionDb);
  }

  public PlanPreferences getPlanPreferences(Session session) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager);
    return sessionDb.getCustomer().getPlan().getPlanPreferences();
  }

  @Transactional
  public void updateSpecifications(
      SnackOrderSpecification snackOrderSpecification,
      AbstractFoodSpecification oldFoodSpecification) {
    entityManager.get().merge(snackOrderSpecification);
    entityManager.get().flush(); // needed after merge to preserve order of items
    entityManager.get().remove(oldFoodSpecification);
  }

  //	private boolean isSnackMarked(Customer customer, Snack snack, String dayString) {
  //		if(snack.getSnackProperty() == SnackProperty.REST){
  //			//in case of rest snack we need to check if user already had a rest snack
  //			TypedQuery<SnackHistory> query = entityManager.get().createQuery("SELECT s FROM SnackHistory
  // s WHERE s.dayString = :dayString AND s.customer = :customer",
  // SnackHistory.class).setParameter("dayString", dayString).setParameter("customer", customer);
  //			List<SnackHistory> result = query.getResultList();
  //			for(SnackHistory snackHistory : result){
  //				if(snackHistory.getPlannedSnack().getSnackProperty() == SnackProperty.REST) return true;
  //			}
  //			return false;
  //		}
  //		else{
  //			TypedQuery<SnackHistory> query = entityManager.get().createQuery("SELECT s FROM SnackHistory
  // s WHERE s.plannedSnack = :snack AND s.dayString = :dayString AND s.customer = :customer",
  // SnackHistory.class).setParameter("snack", snack).setParameter("dayString",
  // dayString).setParameter("customer", customer);
  //			try{
  //				SnackHistory snackHistory = query.getSingleResult();
  //				return snackHistory.getStatus() == SnackStatus.CONSUMED || snackHistory.getStatus() ==
  // SnackStatus.SKIPPED;
  //			}
  //			catch(Exception e){
  //				return false;
  //			}
  //		}
  //	}

  public SnackOrderSpecification getSnackOrderSpecification(Session session) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager);
    return sessionDb.getCustomer().getPlan().getSnackOrderSpecification();
  }

  public Set<FoodItemType> getIngredients(Session session, String dateString) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager);
    SnackMenu snackMenu = sessionDb.getCustomer().getPlan().getSnackMenu();

    Set<FoodItemType> foodItemTypes = new HashSet<>();

    for (Snack snack : snackMenu.getSnacks()) {
      if (snack instanceof GeneralVaryingSnack) {
        for (Snack s : ((GeneralVaryingSnack) snack).getSnacks()) {
          addFoodItems(foodItemTypes, s);
        }
      }
      addFoodItems(foodItemTypes, snack);
    }
    return foodItemTypes;
  }

  private void addFoodItems(Set<FoodItemType> foodItemTypes, Snack snack) {
    for (FoodItem foodItem : snack.getFoodItems()) {
      foodItemTypes.add(foodItem.getFoodType());
    }
  }

  public List<String> getMenuSummary(Session session, String dateString) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid(), entityManager);
    SnackMenu snackMenu = sessionDb.getCustomer().getPlan().getSnackMenu();

    List<String> snackSummary = new ArrayList<>();

    for (Snack snack : snackMenu.getSnacks()) {
      snackSummary.add(snack.getSummary());
    }
    return snackSummary;
  }
}
