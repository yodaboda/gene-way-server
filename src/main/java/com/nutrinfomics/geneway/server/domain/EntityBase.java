package com.nutrinfomics.geneway.server.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Version;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class EntityBase implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  //    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pkGen")
  //    @TableGenerator(allocationSize = 1, initialValue = 0, name = "pkGen", table =
  // "PRIMARY_KEYS")
  private long id;

  @Version private long version;

  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
