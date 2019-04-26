package com.nicomadry.Banking.itl.util;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

  public static String getSalt()
  {
    return BCrypt.gensalt(12);
  }

  public static String hashPassword(String password, String salt)
  {
    return BCrypt.hashpw(password, salt);
  }

  public static boolean checkPassword(String plainPassword, String storedPassword)
  {
    boolean verified;

    if (storedPassword == null || !storedPassword.startsWith("$2a$"))
      throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

    verified = BCrypt.checkpw(plainPassword, storedPassword);

    return verified;
  }

}
