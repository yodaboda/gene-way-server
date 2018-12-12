package com.nutrinfomics.geneway.server.alerts.codeSMS;

import java.util.Locale;

import com.geneway.alerts.AlertLocalization;
import com.nutrinfomics.geneway.server.ResourceBundles;

// TODO: The two classes in this package should potentially be deleted.
public class GeneWayCodeSMSAlertLocalization implements AlertLocalization {

  private ResourceBundles resourceBundles;

  @Override
  public String localizeBody(String... body) {
    String nickName = body[0];
    String code = body[1];
    return resourceBundles.getGeneWayResource("dear", this.getLocale())
        + " "
        + nickName
        + ". "
        + resourceBundles.getGeneWayResource("yourCodeIs", this.getLocale())
        + ": "
        + code;
  }

  @Override
  public Locale getLocale() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String localizeSubject(String arg0) {
    // TODO Auto-generated method stub
    return null;
  }
}
