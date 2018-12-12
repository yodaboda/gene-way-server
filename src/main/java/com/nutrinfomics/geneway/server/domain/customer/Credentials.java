package com.nutrinfomics.geneway.server.domain.customer;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.mindrot.jbcrypt.BCrypt;

import com.nutrinfomics.geneway.server.domain.EntityBase;

@Entity
public class Credentials extends EntityBase {

  private String hashedPassword;

  @Transient
  @Size(min = 6, max = 18, message = "{credentials.password.size.message}")
  private String password;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String hashPassword() {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public String getHashedPassword() {
    return hashedPassword;
  }

  public void setHashedPassword(String hashedPassword) {
    this.hashedPassword = hashedPassword;
  }

  public boolean checkPassword(String plainTextPassword) {
    if (password != null) {
      return password.equals(plainTextPassword);
    } else {
      if (hashedPassword != null) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
      } else {
        return false;
      }
    }
  }
}
