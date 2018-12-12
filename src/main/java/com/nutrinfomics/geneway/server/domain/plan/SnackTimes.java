package com.nutrinfomics.geneway.server.domain.plan;

import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class SnackTimes extends EntityBase {
  private double timeBetweenSnacks = 2;

  @ElementCollection(fetch = FetchType.LAZY)
  @Temporal(TemporalType.TIME)
  private List<Date> snackTimes;

  public double getTimeBetweenSnacks() {
    return timeBetweenSnacks;
  }

  public void setTimeBetweenSnacks(double timeBetweenSnacks) {
    this.timeBetweenSnacks = timeBetweenSnacks;
  }

  public List<Date> getSnackTimes() {
    return snackTimes;
  }

  public void setSnackTimes(List<Date> snackTimes) {
    this.snackTimes = snackTimes;
  }
}
