package com.nutrinfomics.geneway.server.domain.customer;

import javax.persistence.Entity;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class SimpleDate extends EntityBase {
  private int day;
  private int month;
  private int year;

  public SimpleDate() {}

  public SimpleDate(int day, int month, int year) {
    this.day = day;
    this.month = month;
    this.year = year;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }
}
