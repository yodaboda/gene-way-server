package com.nutrinfomics.geneway.server.requestfactory;

import java.util.Locale;

import javax.validation.MessageInterpolator;

// needed for supporting validation error message localization
public class GeneWayLocaleMessageInterpolator implements MessageInterpolator {

  MessageInterpolator defaultInterpolator;
  Locale gwtClientLocale;

  public GeneWayLocaleMessageInterpolator(
      MessageInterpolator defaultInterpolator, Locale gwtClientLocale) {
    this.defaultInterpolator = defaultInterpolator;
    this.gwtClientLocale = gwtClientLocale;
  }

  @Override
  public String interpolate(String messageTemplate, Context context) {
    return defaultInterpolator.interpolate(messageTemplate, context, gwtClientLocale);
  }

  @Override
  public String interpolate(String messageTemplate, Context context, Locale locale) {
    return defaultInterpolator.interpolate(messageTemplate, context, gwtClientLocale);
  }
}
