package com.nutrinfomics.geneway.server;

import javax.inject.Singleton;

import org.mindrot.jbcrypt.BCrypt;

@Singleton
public class PasswordUtils {

  public String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public boolean checkPassword(String plainTextPassword, String password) {
    if (password != null) {
      return password.equals(plainTextPassword);
    } else {
      return false;
    }
  }

  public boolean checkHashedPassword(String plainTextPassword, String hashedPassword) {

    if (hashedPassword != null) {
      return BCrypt.checkpw(plainTextPassword, hashedPassword);
    } else {
      return false;
    }
  }
}
