/*
 * Copyright 2019 Firas Swidan†
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nutrinfomics.geneway.server.requestfactory.request;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import sk.nociar.jpacloner.JpaCloner;
import sk.nociar.jpacloner.PropertyFilter;

import com.google.inject.persist.Transactional;
import com.google.inject.servlet.RequestScoped;
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

@RequestScoped
public class PlanService {
  private EntityManager entityManager;
  private HibernateUtil hibernateUtil;

  @Inject
  public PlanService(EntityManager entityManager, HibernateUtil hibernateUtil) {
    this.entityManager = entityManager;
    this.hibernateUtil = hibernateUtil;
  }

  @Transactional
  public void setDemo(Session session) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid());
    //		sessionDb.getCustomer().getDevice().setCode("demo");

    //		PersonalDetails personalDetails = new PersonalDetails();
    //		personalDetails.setGender(Gender.FEMALE);
    //		personalDetails.setBirthday(new SimpleDate(28, 7, 1975));
    //		personalDetails.setCustomer(sessionDb.getCustomer());
    //		sessionDb.getCustomer().setPersonalDetails(personalDetails);

    Customer demoCustomer = entityManager.find(Customer.class, new Long(4));

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
    Session sessionDb = hibernateUtil.selectSession(session.getSid());
    return sessionDb.getCustomer().getPlan().getPlanPreferences();
  }

  @Transactional
  public void updateSpecifications(
      SnackOrderSpecification snackOrderSpecification,
      AbstractFoodSpecification oldFoodSpecification) {
    entityManager.merge(snackOrderSpecification);
    entityManager.flush(); // needed after merge to preserve order of items
    entityManager.remove(oldFoodSpecification);
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
    Session sessionDb = hibernateUtil.selectSession(session.getSid());
    return sessionDb.getCustomer().getPlan().getSnackOrderSpecification();
  }

  public Set<FoodItemType> getIngredients(Session session, String dateString) {
    Session sessionDb = hibernateUtil.selectSession(session.getSid());
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
    Session sessionDb = hibernateUtil.selectSession(session.getSid());
    SnackMenu snackMenu = sessionDb.getCustomer().getPlan().getSnackMenu();

    List<String> snackSummary = new ArrayList<>();

    for (Snack snack : snackMenu.getSnacks()) {
      snackSummary.add(snack.getSummary());
    }
    return snackSummary;
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */
