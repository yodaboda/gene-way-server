package com.nutrinfomics.geneway.server.domain.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.shared.SnackProperty;

@Entity
public class SnackMenu extends EntityBase implements Serializable{
	
	@OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
	private List<Snack> snacks;

	public List<Snack> getSnacks() {
		return snacks;
	}

	public void setSnacks(List<Snack> snacks) {
		this.snacks = snacks;
	}

	
	public SnackMenu(){
		
	}
	public SnackMenu(Vector<Snack> snacks){
		setSnacks(snacks);
	}

	public SnackProperty getSnackProperty(int rowIndex) {
		return getSnack(rowIndex).getSnackProperty();
	}

	public Snack getSnack(int rowIndex) {
		return getSnacks().get(rowIndex);
	}
	public Date getTime(int rowIndex) {
		return getSnack(rowIndex).getTime();
	}

	public void setTime(int rowIndex, Date date){
		getSnack(rowIndex).setTime(date);
	}
	public int size() {
		return getSnacks().size();
	}

}
