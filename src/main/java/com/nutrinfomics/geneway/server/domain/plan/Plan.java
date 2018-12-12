package com.nutrinfomics.geneway.server.domain.plan;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.specification.AcceptAllSpecification;
import com.nutrinfomics.geneway.server.domain.specification.SnackOrderSpecification;
import com.nutrinfomics.geneway.shared.ActivitiesType;
import com.nutrinfomics.geneway.shared.SupplementType;

@Entity
public class Plan extends EntityBase implements Serializable {
  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private SnackMenu snackMenu;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private MarkedSnackMenu todaysSnackMenu;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private SnackOrderSpecification snackOrderSpecification;

  @ElementCollection(targetClass = ActivitiesType.class)
  @Enumerated(EnumType.STRING) // Possibly optional (I'm not sure) but defaults to ORDINAL.
  @CollectionTable(name = "plan_activities")
  @Column(name = "activities") // Column name in plan_activities
  private List<ActivitiesType> activities;

  @ElementCollection(targetClass = SupplementType.class)
  @Enumerated(EnumType.STRING) // Possibly optional (I'm not sure) but defaults to ORDINAL.
  @CollectionTable(name = "plan_supplements")
  @Column(name = "supplements") // Column name in plan_supplements
  private List<SupplementType> supplements;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private PlanPreferences planPreferences;

  public Plan() {}

  public SnackOrderSpecification getSnackOrderSpecification() {
    return snackOrderSpecification;
  }

  public void setSnackOrderSpecification(SnackOrderSpecification snackOrderSpecification) {
    this.snackOrderSpecification = snackOrderSpecification;
  }

  public SnackMenu getSnackMenu() {
    return snackMenu;
  }

  public void setSnackMenu(SnackMenu snackMenu) {
    this.snackMenu = snackMenu;
  }

  public List<ActivitiesType> getActivities() {
    return activities;
  }

  public void setActivities(List<ActivitiesType> activities) {
    this.activities = activities;
  }

  public List<SupplementType> getSupplements() {
    return supplements;
  }

  public void setSupplements(List<SupplementType> supplements) {
    this.supplements = supplements;
  }

  public Plan(
      SnackMenu snackMenu,
      Vector<ActivitiesType> activities,
      Vector<SupplementType> supplements,
      PlanPreferences planPreferences) {
    this.snackMenu = snackMenu;
    this.activities = activities;
    this.planPreferences = planPreferences;
    snackOrderSpecification = new SnackOrderSpecification(snackMenu.size());
    for (int i = 0; i < snackMenu.size(); ++i) {
      snackOrderSpecification.getFoodOrderSpecification().add(new AcceptAllSpecification());
    }
  }

  public PlanPreferences getPlanPreferences() {
    return planPreferences;
  }

  public void setPlanPreferences(PlanPreferences planPreferences) {
    this.planPreferences = planPreferences;
  }

  public MarkedSnackMenu getTodaysSnackMenu() {
    return todaysSnackMenu;
  }

  public void setTodaysSnackMenu(MarkedSnackMenu todaysSnackMenu) {
    this.todaysSnackMenu = todaysSnackMenu;
  }
}
