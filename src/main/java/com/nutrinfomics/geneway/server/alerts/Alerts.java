package com.nutrinfomics.geneway.server.alerts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

@Singleton
public class Alerts {
  /** Logger for unexpected events. */
  private static final Logger LOGGER = LogManager.getLogger();

  private static final String PATH_TO_DETECTOR_PROFILE =
      System.getProperty("user.dir") + "/langdetect/profiles/";

  static {
    try {
      DetectorFactory.loadProfile(PATH_TO_DETECTOR_PROFILE);
    } catch (LangDetectException e1) {
      LOGGER.log(Level.FATAL, e1.toString(), e1);
    }
  }

  private Map<Long, ScheduledAlert> scheduledAlertMapping = new HashMap<>();

  public Alerts() {
    List<String> langs = DetectorFactory.getLangList();
    LOGGER.log(Level.INFO, "Detection supported for the following languages:");
    for (String lang : langs) {
      LOGGER.log(Level.INFO, lang);
    }
  }
  //	public Alert createAlert(Customer customer, Snack snack, boolean sameDay, String email){
  //		List<AlertType> alertTypes = new ArrayList<>();
  // 		if(customer.getPlan().getPlanPreferences().isEmailAlerts()){
  // 			alertTypes.add(AlertType.EMAIL);
  //		}
  //		if(customer.getPlan().getPlanPreferences().isSmsAlerts()){
  //			alertTypes.add(AlertType.SMS);
  //		}
  //
  //		double inHours =
  // customer.getPlan().getPlanPreferences().getSnackTimes().getTimeBetweenSnacks();
  //		if(!sameDay){
  //			LocalDate tomorrow = LocalDate.now().plusDays(1);
  //			LocalTime mealTime = LocalTime.of(8, 0, 0);
  //			LocalDateTime mealDateTime = LocalDateTime.of(tomorrow, mealTime);
  //
  //			LocalDateTime now = LocalDateTime.now();
  //
  //			Duration duration = Duration.between(now, mealDateTime);
  //			inHours = duration.toHours();
  //		}
  //
  //		UserAlert userAlert = new ScheduledAlert(customer, inHours, snack, alertTypes, email);
  //
  //		snackAlertMapping.put(snack.getId(), userAlert);
  //
  //		return userAlert;
  //	}

  public ScheduledAlert addAlert(long key, ScheduledAlert scheduledAlert) {
    return scheduledAlertMapping.put(key, scheduledAlert);
  }

  public ScheduledAlert getAlert(long key) {
    return scheduledAlertMapping.get(key);
  }

  public ScheduledAlert removeAlert(long key) {
    return scheduledAlertMapping.remove(key);
  }
}
