package com.nutrinfomics.geneway.server.requestfactory.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.web.bindery.requestfactory.shared.Request;
import com.nutrinfomics.geneway.server.alert.Alerts;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnack;
import com.nutrinfomics.geneway.server.domain.plan.MarkedSnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackHistory;
import com.nutrinfomics.geneway.server.domain.plan.SnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.VaryingSnack;
import com.nutrinfomics.geneway.server.domain.specification.AbstractFoodSpecification;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;
import com.nutrinfomics.geneway.shared.FoodItemType;

public class PlanService {
	@Inject Provider<EntityManager> entityManager;
	
	public PlanPreferences getPlanPreferences(Session session){
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		return sessionDb.getCustomer().getPlan().getPlanPreferences();
	}
	
	private MarkedSnackMenu calcTodaysSnackMenu(Session sessionDb, String dateString){
		Plan plan = sessionDb.getCustomer().getPlan();
		SnackMenu snackMenu = plan.getSnackMenu();

		List<Snack> snacks = new ArrayList<>();
		
		for(Snack snack : snackMenu.getSnacks()){
			if(snack instanceof VaryingSnack){
				Snack resultSnack = getTodaysSnack((VaryingSnack)snack);
				snack = resultSnack;
			}
			snacks.add(snack);
		}

		MarkedSnackMenu todaysSnackMenu = new MarkedSnackMenu(dateString, snacks);
		
		return todaysSnackMenu;
	}
	
	@Transactional
	public Snack getNextSnack(Session session, String dateString){
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		
		if(sessionDb.getCustomer().getPlan().getTodaysSnackMenu() == null){
			setTodaysSnackMenu(sessionDb, dateString);
		}
		
		Snack nextSnack = calcNextSnack(sessionDb);
		if(nextSnack == null){
			setTodaysSnackMenu(sessionDb, dateString);
			nextSnack = calcNextSnack(sessionDb);
		}
		
		return nextSnack;
	}

	
	private void setTodaysSnackMenu(Session sessionDb, String dateString) {
		MarkedSnackMenu markedSnackMenu = calcTodaysSnackMenu(sessionDb, dateString);
		Plan plan = sessionDb.getCustomer().getPlan();
		entityManager.get().merge(markedSnackMenu);
		plan.setTodaysSnackMenu(markedSnackMenu);
//		entityManager.get().merge(plan);
	}

	@Transactional
	public void markCurrentSnack(Session session, Snack snack, SnackHistory snackHistory){
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		MarkedSnackMenu todaysSnackMenu = sessionDb.getCustomer().getPlan().getTodaysSnackMenu();

		MarkedSnack markedSnack = todaysSnackMenu.calcCurrentSnack();
		markedSnack.setMarked(true);
		Alerts.getInstance().getSnackAlert(markedSnack.getSnack().getId()).cancel();
		
//		entityManager.get().merge(todaysSnackMenu);

		entityManager.get().merge(snackHistory);
	}

	private Snack calcNextSnack(Session sessionDb) {
		MarkedSnackMenu todaysSnackMenu = sessionDb.getCustomer().getPlan().getTodaysSnackMenu();

		MarkedSnack markedSnack = todaysSnackMenu.calcCurrentSnack();

		if(markedSnack != null){
			Alerts.getInstance().createAlert(sessionDb.getCustomer(), markedSnack.getSnack());
			return markedSnack.getSnack();
		}
		
		return null;
	}

	@Transactional
	private Snack getTodaysSnack(VaryingSnack varyingSnack){
		try{
		List<Integer> indices = new ArrayList<>(7);
			for(int i = 0; i < 7; ++i) indices.add(i);
			Collections.shuffle(indices);

			for(int i = 0; i <indices.size(); ++i){
				if(!varyingSnack.isEaten(indices.get(i))){
					varyingSnack.setEaten(indices.get(i), true);
					return varyingSnack.getWeeklySnacks().get(indices.get(i));
				}
			}

			//otherwise, all snacks already eaten - end of week - reset
			for(int i = 0; i < 7; ++i) varyingSnack.setEaten(i, false);

			varyingSnack.setEaten(indices.get(0), true);
			return varyingSnack.getWeeklySnacks().get(indices.get(0));
		}
		finally{
			entityManager.get().merge(varyingSnack);
		}
	}
	
	@Transactional
	public void updateSpecifications(SnackOrderSpecification snackOrderSpecification,
										AbstractFoodSpecification oldFoodSpecification){
		entityManager.get().merge(snackOrderSpecification);
		entityManager.get().remove(oldFoodSpecification);
	}

	
//	private boolean isSnackMarked(Customer customer, Snack snack, String dayString) {
//		if(snack.getSnackProperty() == SnackProperty.REST){
//			//in case of rest snack we need to check if user already had a rest snack
//			TypedQuery<SnackHistory> query = entityManager.get().createQuery("SELECT s FROM SnackHistory s WHERE s.dayString = :dayString AND s.customer = :customer", SnackHistory.class).setParameter("dayString", dayString).setParameter("customer", customer);
//			List<SnackHistory> result = query.getResultList();
//			for(SnackHistory snackHistory : result){
//				if(snackHistory.getPlannedSnack().getSnackProperty() == SnackProperty.REST) return true;
//			}
//			return false;
//		}
//		else{
//			TypedQuery<SnackHistory> query = entityManager.get().createQuery("SELECT s FROM SnackHistory s WHERE s.plannedSnack = :snack AND s.dayString = :dayString AND s.customer = :customer", SnackHistory.class).setParameter("snack", snack).setParameter("dayString", dayString).setParameter("customer", customer);
//			try{
//				SnackHistory snackHistory = query.getSingleResult();
//				return snackHistory.getStatus() == SnackStatus.CONSUMED || snackHistory.getStatus() == SnackStatus.SKIPPED;
//			}
//			catch(Exception e){
//				return false;
//			}
//		}
//	}

	
	public SnackOrderSpecification getSnackOrderSpecification(Session session){
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		return sessionDb.getCustomer().getPlan().getSnackOrderSpecification();
	}
	
	public Set<FoodItemType> getIngredients(Session session, String dateString){
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		SnackMenu snackMenu = sessionDb.getCustomer().getPlan().getSnackMenu();

		Set<FoodItemType> foodItemTypes = new HashSet<>();
		
		for(Snack snack : snackMenu.getSnacks()){
			for(FoodItem foodItem : snack.getFoodItems()){
				foodItemTypes.add(foodItem.getFoodType());
			}
		}
		return foodItemTypes;
	}

	public List<String> getMenuSummary(Session session, String dateString){
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		SnackMenu snackMenu = sessionDb.getCustomer().getPlan().getSnackMenu();

		List<String> snackSummary = new ArrayList<>();
		
		for(Snack snack : snackMenu.getSnacks()){
			snackSummary.add(snack.getSummary());
		}
		return snackSummary;
	}
}
