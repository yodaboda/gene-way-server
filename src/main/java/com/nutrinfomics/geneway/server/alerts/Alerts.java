/*
 * Copyright 2019 Firas Swidan†
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */
