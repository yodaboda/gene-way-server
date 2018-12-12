package com.nutrinfomics.geneway.server;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
