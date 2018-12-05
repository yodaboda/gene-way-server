package com.nutrinfomics.geneway.server.domain.plan;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class MarkedSnack extends EntityBase {

	@OneToOne(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
	private Snack snack;
	
	private boolean marked = false;
	
	public MarkedSnack(Snack snack){
		this.snack = snack;
	}
	
	public MarkedSnack(){
		
	}
	
	public Snack getSnack() {
		return snack;
	}

	public void setSnack(Snack snack) {
		this.snack = snack;
	}

	public boolean isMarked(){
		return marked;
	}
	
	public void setMarked(boolean marked){
		this.marked = marked;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		MarkedSnack markedSnack = (MarkedSnack)obj;
		return getSnack().equals(markedSnack.getSnack());
	}
	
	@Override
	public int hashCode() {
		return getSnack().hashCode();
	}
}
