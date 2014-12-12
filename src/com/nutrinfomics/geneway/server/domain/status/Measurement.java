package com.nutrinfomics.geneway.server.domain.status;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.shared.MeasurementUnit;

@Entity
public class Measurement extends EntityBase{
	
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@Enumerated(EnumType.STRING)
	private MeasurementType type;

	private double value;

	@Enumerated(EnumType.STRING)
	private MeasurementUnit unit;
	
	public MeasurementUnit getUnit() {
		return unit;
	}

	public void setUnit(MeasurementUnit unit) {
		this.unit = unit;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public MeasurementType getType() {
		return type;
	}

	public void setType(MeasurementType type) {
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
