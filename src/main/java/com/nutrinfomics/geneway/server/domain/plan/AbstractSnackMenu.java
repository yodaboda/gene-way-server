package com.nutrinfomics.geneway.server.domain.plan;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public abstract class AbstractSnackMenu extends EntityBase {

  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.ALL})
  private List<Snack> snacks;

  public List<Snack> getSnacks() {
    return snacks;
  }

  public void setSnacks(List<Snack> snacks) {
    this.snacks = snacks;
  }
}
