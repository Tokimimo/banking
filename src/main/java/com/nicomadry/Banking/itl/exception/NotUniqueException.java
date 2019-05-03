package com.nicomadry.Banking.itl.exception;

/**
 * This exception is thrown to indicate that the requested resource couldn't be retrieved because the given information didn't return a
 * unique result, or if a entity couldn't be created since it would violate a unique constraint.
 */
public class NotUniqueException extends Exception {

  public NotUniqueException( String message )
  {
    super( message );
  }

  public NotUniqueException( String message, Throwable throwable )
  {
    super( message, throwable );
  }

}
