package com.nutrinfomics.geneway.server.domain.plan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.nutrinfomics.geneway.server.domain.EntityBase;
import com.nutrinfomics.geneway.server.domain.customer.Customer;
import com.nutrinfomics.geneway.shared.SnackStatus;

@Entity
@Table(
    indexes = {
      @Index(columnList = "customer, plannedSnack, dayString", unique = true),
      @Index(columnList = "customer, dayString", unique = false)
    })
public class SnackHistory extends EntityBase {

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "customer")
  @NotNull
  private Customer customer;

  @OneToOne(fetch = FetchType.EAGER) // no cascade
  @JoinColumn(name = "plannedSnack")
  private Snack plannedSnack;

  @OneToOne(fetch = FetchType.EAGER) // no cascade
  @JoinColumn(name = "eatenSnack")
  private Snack eatenSnack;

  @Column(name = "dayString")
  private String dayString;

  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp;

  private int timeZoneDiff;

  @Enumerated(EnumType.STRING)
  private SnackStatus status;

  public Snack getPlannedSnack() {
    return plannedSnack;
  }

  public void setPlannedSnack(Snack plannedSnack) {
    this.plannedSnack = plannedSnack;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Snack getEatenSnack() {
    return eatenSnack;
  }

  public void setEatenSnack(Snack eatenSnack) {
    this.eatenSnack = eatenSnack;
  }

  public String getDayString() {
    return dayString;
  }

  public void setDayString(String dayString) {
    this.dayString = dayString;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public int getTimeZoneDiff() {
    return timeZoneDiff;
  }

  public void setTimeZoneDiff(int timeZoneDiff) {
    this.timeZoneDiff = timeZoneDiff;
  }

  public SnackStatus getStatus() {
    return status;
  }

  public void setStatus(SnackStatus status) {
    this.status = status;
  }

  public static String getDateString(Date timestamp, int timeZoneOffset) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(timestamp);
    calendar.set(Calendar.ZONE_OFFSET, timeZoneOffset);

    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    System.out.println(hour);

    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy"); //
    formatter.setCalendar(calendar);

    String dateString = formatter.format(calendar.getTime());
    return dateString;
  }
}
