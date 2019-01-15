package com.nutrinfomics.geneway.server.phoneVerifier;

public interface PhoneNumberVerifier {
  public boolean isLegal(String phoneNumber);
}
