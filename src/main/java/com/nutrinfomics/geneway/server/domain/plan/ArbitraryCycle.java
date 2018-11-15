package com.nutrinfomics.geneway.server.domain.plan;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Embeddable
public class ArbitraryCycle extends EntityBase implements Repetition {

	private int cycleLength;
	private int remainingLength;
	
	public ArbitraryCycle(){
		
	}
	
	public ArbitraryCycle(ArbitraryCycle cycle){
		this(cycle.cycleLength);
	}
	
	public ArbitraryCycle(int cycleLength){
		setCycleLength(cycleLength);
	}
	
	@Override
	public int getCycleLength() {
		return cycleLength;
	}

	public void setCycleLength(int cycleLength){
		this.cycleLength = cycleLength;
		remainingLength = cycleLength;
	}
	
	@Override
	public int getRemainingLength() {
		return remainingLength;
	}

	@Override
	public void advanceBySingleUnit() {
		remainingLength--;
	}

	public void setRemainingLength(int remainingLength){
		this.remainingLength = remainingLength;
	}
	
	public boolean isToBeUsed(){
		return getRemainingLength() >= 1;
	}
	
	public void reset(){
		setRemainingLength(getCycleLength());
	}
}
