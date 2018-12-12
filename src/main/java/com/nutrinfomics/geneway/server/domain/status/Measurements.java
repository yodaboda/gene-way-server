package com.nutrinfomics.geneway.server.domain.status;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class Measurements extends EntityBase {

  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.ALL})
  private List<Measurement> measurements;

  public List<Measurement> getMeasurements() {
    return measurements;
  }

  public void setMeasurements(List<Measurement> measurements) {
    this.measurements = measurements;
  }
}
