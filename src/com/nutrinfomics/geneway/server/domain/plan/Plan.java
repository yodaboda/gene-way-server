package com.nutrinfomics.geneway.server.domain.plan;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import com.nutrinfomics.geneway.server.domain.ModelObject;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.shared.ActivitiesType;
import com.nutrinfomics.geneway.shared.SupplementType;


public class Plan extends ModelObject implements Serializable {

	private SnackMenu snackMenu;
	private List<ActivitiesType> activities;
	private List<SupplementType> supplements;
	
	public Plan(){
		
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.PlanProxy#getSnackMenu()
	 */
	public SnackMenu getSnackMenu() {
		return snackMenu;
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.PlanProxy#setSnackMenu(com.nutrinfomics.geneway.server.domain.plan.SnackMenu)
	 */
	public void setSnackMenu(SnackMenu snackMenu) {
		this.snackMenu = snackMenu;
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.PlanProxy#getActivities()
	 */
	public List<ActivitiesType> getActivities() {
		return activities;
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.PlanProxy#setActivities(java.util.Vector)
	 */
	public void setActivities(List<ActivitiesType> activities) {
		this.activities = activities;
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.PlanProxy#getSupplements()
	 */
	public List<SupplementType> getSupplements() {
		return supplements;
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.PlanProxy#setSupplements(java.util.Vector)
	 */
	public void setSupplements(List<SupplementType> supplements) {
		this.supplements = supplements;
	}
	public Plan(SnackMenu snackMenu, Vector<ActivitiesType> activities,
				Vector<SupplementType> supplements){
		this.snackMenu = snackMenu;
		this.activities = activities;
	}

	static public Plan findPlanForSession(Session session){
		return getPlanForUsername("فراس سويدان");
	}
	
	static public Plan findPlan(long id){
		return findPlanForSession(null);
	}
	
	static private Plan getPlanForUsername(String username) {
		String filePath = "/home/firas/Documents/plans/" + username + "/snackMenu.ser";

		SnackMenu snackMenu = null;
		try(
			      InputStream file = new FileInputStream(filePath);
			      InputStream buffer = new BufferedInputStream(file);
			      ObjectInput input = new ObjectInputStream (buffer);
			    ){
			snackMenu = (SnackMenu)input.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buildPlanFromSnackMenu(snackMenu);
	}

	static private Plan buildPlanFromSnackMenu(SnackMenu snackMenu) {
		Plan plan = new Plan();
		plan.setSnackMenu(snackMenu);
		return plan;
	}

}
