package com.nutrinfomics.geneway.server.domain.device;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;

@Entity
public class Device extends EntityBase implements Serializable {
  @Column(nullable = false, unique = true)
  private String uuid;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "device")
  private Customer customer;

  private String code;

  private OffsetDateTime codeCreationTimestamp;

  public OffsetDateTime getCodeCreationTimestamp() {
    return codeCreationTimestamp;
  }

  public void setCodeCreationTimestamp(OffsetDateTime codeCreation) {
    this.codeCreationTimestamp = codeCreation;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }
}
