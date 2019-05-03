package com.nicomadry.Banking.itl.util;


import org.mindrot.jbcrypt.BCrypt;

/**
 * This utils class holds helper methods to handle password encryption and checking. Additionally it encapsulates the used BCrypt API to
 * make it interchangeable.
 */
public class PasswordUtils {

  /**
   * Creates a new password salt, which went through 12 generation iterations.
   *
   * @return The created password salt
   */
  public static String getSalt()
  {
    return BCrypt.gensalt( 12 );
  }

  /**
   * Hashes the given password with the given salt and returns the encrypted password.
   *
   * @param password The password to encrypt
   * @param salt     The salt to use for encryption
   *
   * @return The encrypted password
   */
  public static String hashPassword( String password, String salt )
  {
    return BCrypt.hashpw( password, salt );
  }

  /**
   * Checks the given clear text password against the encrypted password and if a certain percentage of matching is reached, declares the
   * password as valid.
   *
   * @param plainPassword  The clear text password the check
   * @param storedPassword The encrypted password to check against
   *
   * @return True, if the password is matching and valid, false otherwise.
   */
  public static boolean checkPassword( String plainPassword, String storedPassword )
  {
    boolean verified;

    if ( storedPassword == null || !storedPassword.startsWith( "$2a$" ) ) {
      throw new java.lang.IllegalArgumentException( "Invalid hash provided for comparison" );
    }

    verified = BCrypt.checkpw( plainPassword, storedPassword );

    return verified;
  }

}
