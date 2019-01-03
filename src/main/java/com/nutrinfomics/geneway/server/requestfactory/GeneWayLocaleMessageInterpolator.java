package com.nutrinfomics.geneway.server.requestfactory;

import java.util.Locale;

import javax.validation.MessageInterpolator;

// needed for supporting validation error message localization
public class GeneWayLocaleMessageInterpolator implements MessageInterpolator {

  MessageInterpolator defaultInterpolator;
  Locale requesLocale;

  public GeneWayLocaleMessageInterpolator(
      MessageInterpolator defaultInterpolator, Locale requestLocale) {
    this.defaultInterpolator = defaultInterpolator;
    this.requesLocale = requestLocale;
  }

  @Override
  public String interpolate(String messageTemplate, Context context) {
    return defaultInterpolator.interpolate(messageTemplate, context, requesLocale);
  }

  @Override
  public String interpolate(String messageTemplate, Context context, Locale locale) {
    return defaultInterpolator.interpolate(messageTemplate, context, requesLocale);
  }
}
