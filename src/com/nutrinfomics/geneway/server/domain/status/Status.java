package com.nutrinfomics.geneway.server.domain.status;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class Status extends EntityBase{
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Measurements measurements;

	public Measurements getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Measurements measurements) {
		this.measurements = measurements;
	}
}
