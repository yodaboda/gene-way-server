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

package com.nutrinfomics.geneway.server;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class Utils {
  private static final Logger LOGGER = LogManager.getLogger();
  private RequestUtils requestUtils;

  @Inject
  public Utils(RequestUtils requestUtils) {
    this.requestUtils = requestUtils;
  }
  /**
   * Gets the locale from the HTTP request.
   *
   * @return HTTP request locale or English otherwise
   */
  public Locale getLocale() {
    HttpServletRequest threadLocalRequest = requestUtils.getHttpServletRequest();
    if (threadLocalRequest != null) {
      Cookie[] cookies = threadLocalRequest.getCookies();
      if (cookies != null) {
        boolean hasGWTLocaleCookie = false;
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals("gwtLocale")) {
            hasGWTLocaleCookie = true;
            if (cookie.getValue() == null) {
              LOGGER.log(Level.WARN, "Null gwtLocale cookie value!");
            } else {
              try {
                return LocaleUtils.toLocale(cookie.getValue());
              } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.FATAL, ex.toString(), ex);
              }
            }
          }
        }
        if (!hasGWTLocaleCookie) {
          LOGGER.log(Level.WARN, "Http Request has no gwtLocale cookie!");
        }
      } else {
        LOGGER.log(Level.WARN, "Http Request has null cookies!");
      }
    } else {
      LOGGER.log(Level.WARN, "Null Http Request!");
    }
    return Locale.ENGLISH;
  }
  /**
   * Gets the IP address from the HTTP request
   *
   * @return client IP address
   */
  public String getIP() {
    HttpServletRequest threadLocalRequest = requestUtils.getHttpServletRequest();
    if (threadLocalRequest != null) {
      return threadLocalRequest.getRemoteAddr();
    } else {
      LOGGER.log(Level.WARN, "Null Http Request!");
      return null;
    }
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */