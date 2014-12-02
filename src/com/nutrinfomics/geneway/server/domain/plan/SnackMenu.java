package com.nutrinfomics.geneway.server.domain.plan;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.nutrinfomics.geneway.server.domain.ModelObject;
import com.nutrinfomics.geneway.shared.SnackProperty;


public class SnackMenu extends ModelObject implements Serializable{
	private List<Snack> snacks;
	
	public SnackMenu(){
		
	}
	public SnackMenu(Vector<Snack> snacks){
		setSnacks(snacks);
	}
	
	private void setSnacks(List<Snack> snacks) {
		this.snacks = snacks;
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackMenuProxy#getSnack(int)
	 */
	public Snack getSnack(int rowIndex) {
		return snacks.get(rowIndex);
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackMenuProxy#getSnackProperty(int)
	 */
	public SnackProperty getSnackProperty(int rowIndex) {
		return getSnack(rowIndex).getSnackProperty();
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackMenuProxy#getTime(int)
	 */
	public Date getTime(int rowIndex) {
		return getSnack(rowIndex).getTime();
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackMenuProxy#setTime(int, java.util.Date)
	 */
	public void setTime(int rowIndex, Date date){
		getSnack(rowIndex).setTime(date);
	}
	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackMenuProxy#size()
	 */
	public int size() {
		return snacks.size();
	}

	/* (non-Javadoc)
	 * @see com.nutrinfomics.geneway.server.domain.plan.SnackMenuProxy#getSnacks()
	 */
	public List<Snack> getSnacks() {
		return snacks;
	}
	
	static public SnackMenu findSnackMenu(long id){
		return Plan.findPlan(id).getSnackMenu();
	}
}
