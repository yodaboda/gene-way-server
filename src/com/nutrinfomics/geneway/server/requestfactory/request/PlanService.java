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
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.domain.plan.FoodItem;
import com.nutrinfomics.geneway.server.domain.plan.Plan;
import com.nutrinfomics.geneway.server.domain.plan.PlanPreferences;
import com.nutrinfomics.geneway.server.domain.plan.Snack;
import com.nutrinfomics.geneway.server.domain.plan.SnackHistory;
import com.nutrinfomics.geneway.server.domain.plan.SnackMenu;
import com.nutrinfomics.geneway.server.domain.plan.VaryingSnack;
import com.nutrinfomics.geneway.shared.FoodItemType;
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
			if(! isSnackMarked(snack, dateString)){
				SnackHistory.setPlannedSnackValue(snack);
				if(snack instanceof VaryingSnack){
					Snack resultSnack = getTodaysSnack((VaryingSnack)snack);
					snack = resultSnack;
				}
				
				Alerts.getInstance().add(sessionDb.getCustomer(), snack);
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
	
	
	private boolean isSnackMarked(Snack snack, String dayString) {
		TypedQuery<SnackHistory> query = entityManager.get().createQuery("SELECT s FROM SnackHistory s WHERE s.plannedSnack = :snack AND s.dayString = :dayString", SnackHistory.class).setParameter("snack", snack).setParameter("dayString", dayString);
		try{
			SnackHistory snackHistory = query.getSingleResult();
			return snackHistory.getStatus() == SnackStatus.CONSUMED || snackHistory.getStatus() == SnackStatus.SKIPPED;
		}
		catch(Exception e){
			return false;
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

	
//	static public Plan findPlanForSession(Session session){
////		HibernateUtil.getInstance().getEntityManager().find(Plan.class, 2);
//		return getPlanForUsername("فراس سويدان");
//	}
//	
//	static public Plan findPlan(long id){
//		return findPlanForSession(null);
//	}
	
//	static private Plan getPlanForUsername(String username) {
//		String filePath = "/home/firas/Documents/plans/" + username + "/snackMenu.ser";
//
//		SnackMenu snackMenu = null;
//		try(
//			      InputStream file = new FileInputStream(filePath);
//			      InputStream buffer = new BufferedInputStream(file);
//			      ObjectInput input = new ObjectInputStream (buffer);
//			    ){
//			snackMenu = (SnackMenu)input.readObject();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return buildPlanFromSnackMenu(snackMenu);
//	}

	private Plan buildPlanFromSnackMenu(SnackMenu snackMenu) {
		Plan plan = new Plan();
		plan.setSnackMenu(snackMenu);
		return plan;
	}

}
