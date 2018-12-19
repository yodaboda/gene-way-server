package com.nutrinfomics.geneway.server.alerts;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.persistence.Transient;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.domain.EntityBase;

@RequestScoped
public class ScheduledAlert extends EntityBase implements Alert {

  /** Logger for unexpected events. */
  private static final Logger LOGGER = LogManager.getLogger();

  /** */
  private static final long serialVersionUID = -6756667893271410965L;

  @Transient private Alert alert;
  @Transient private ScheduledFuture<?> scheduled;
  @Transient private ScheduledExecutorService schedulerService;

  @Inject
  public ScheduledAlert(Alert alert, ScheduledExecutorService schedulerService) {
    this.alert = alert;
    this.schedulerService = schedulerService;
  }

  public void schedule(double inHours) {
    Runnable runnable = () -> remind();
    scheduled = schedulerService.schedule(runnable, (long) (inHours * 60), TimeUnit.MINUTES);
  }

  public void cancel() {
    if (scheduled != null) {
      scheduled.cancel(false);
    } else {
      LOGGER.log(Level.INFO, "Attempting to cancel a null scheduled field");
    }
  }

  @Override
  public void remind() {
    alert.remind();
  }
}
