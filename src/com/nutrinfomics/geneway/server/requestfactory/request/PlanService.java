package com.nutrinfomics.geneway.server.requestfactory.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.nutrinfomics.geneway.server.alert.Alerts;
import com.nutrinfomics.geneway.server.alert.UserAlert;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackHistory;
import com.nutrinfomics.geneway.server.domain.plan.SnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.VaryingSnack;
import com.nutrinfomics.geneway.shared.FoodItemType;
import com.nutrinfomics.geneway.shared.SnackProperty;
import com.nutrinfomics.geneway.shared.SnackStatus;

public class PlanService {
	@Inject Provider<EntityManager> entityManager;
	
	public PlanPreferences getPlanPreferences(Session session){
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		return sessionDb.getCustomer().getPlan().getPlanPreferences();
	}
	
	public Snack getNextSnack(Session session, String dateString){
		Session sessionDb = new HibernateUtil().selectSession(session.getSid(), entityManager);
		SnackMenu snackMenu = sessionDb.getCustomer().getPlan().getSnackMenu();

		for(Snack snack : snackMenu.getSnacks()){
			if(! isSnackMarked(sessionDb.getCustomer(), snack, dateString)){
				if(snack instanceof VaryingSnack){
					Snack resultSnack = getTodaysSnack((VaryingSnack)snack);
					snack = resultSnack;
				}
				
				UserAlert alert = Alerts.getInstance().createAlert(sessionDb.getCustomer(), snack);
				snack.setAlert(alert);
				return snack;
			}
		}
		return new Snack();
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
	
	
	private boolean isSnackMarked(Customer customer, Snack snack, String dayString) {
		if(snack.getSnackProperty() == SnackProperty.REST){
			//in case of rest snack we need to check if user already had a rest snack
			TypedQuery<SnackHistory> query = entityManager.get().createQuery("SELECT s FROM SnackHistory s WHERE s.dayString = :dayString AND s.customer = :customer", SnackHistory.class).setParameter("dayString", dayString).setParameter("customer", customer);
			List<SnackHistory> result = query.getResultList();
			for(SnackHistory snackHistory : result){
				if(snackHistory.getPlannedSnack().getSnackProperty() == SnackProperty.REST) return true;
			}
			return false;
		}
		else{
			TypedQuery<SnackHistory> query = entityManager.get().createQuery("SELECT s FROM SnackHistory s WHERE s.plannedSnack = :snack AND s.dayString = :dayString AND s.customer = :customer", SnackHistory.class).setParameter("snack", snack).setParameter("dayString", dayString).setParameter("customer", customer);
			try{
				SnackHistory snackHistory = query.getSingleResult();
				return snackHistory.getStatus() == SnackStatus.CONSUMED || snackHistory.getStatus() == SnackStatus.SKIPPED;
			}
			catch(Exception e){
				return false;
			}
		}
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
