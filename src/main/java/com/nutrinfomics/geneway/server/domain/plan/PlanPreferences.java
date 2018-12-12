package com.nutrinfomics.geneway.server.domain.plan;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class PlanPreferences extends EntityBase {
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private SnackTimes snackTimes;

  public SnackTimes getSnackTimes() {
    return snackTimes;
  }

  public void setSnackTimes(SnackTimes snackTimes) {
    this.snackTimes = snackTimes;
  }

  public boolean isSmsAlerts() {
    return smsAlerts;
  }

  public void setSmsAlerts(boolean smsAlerts) {
    this.smsAlerts = smsAlerts;
  }

  public boolean isEmailAlerts() {
    return emailAlerts;
  }

  public void setEmailAlerts(boolean emailAlerts) {
    this.emailAlerts = emailAlerts;
  }

  private boolean smsAlerts;

  private boolean emailAlerts;
}
