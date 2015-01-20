package com.nutrinfomics.geneway.server.domain.plan;

import java.util.BitSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class MarkedSnackMenu extends SnackMenu {

	@Transient
	private BitSet markedSnack = new BitSet(7);
	
	private String date;
	
	public MarkedSnackMenu(String date){
		this.setDate(date);
	}
	
    @Access(AccessType.PROPERTY) // Store the property instead
    @Column(name = "markedSnack")
    byte[] getMarkedSnackInDbRepresentation() {
    	return markedSnack.toByteArray();
    }

    void setMarkedSnackInDbRepresentation(byte[] data) {
        markedSnack = BitSet.valueOf(data);
    }
    
	public boolean isMarkedSnack(Integer integer) {
		return markedSnack.get(integer);
	}

	public void setMarkedSnack(Integer integer, boolean b) {
		markedSnack.set(integer, b);		
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
