package com.nicomadry.Banking.itl.exception;

/**
 * This exception is thrown by all actions which also perform certain data and integrity validations.
 */
public class ValidationException extends Exception {

  public ValidationException( String message )
  {
    super( message );
  }

  public ValidationException( String message, Throwable throwable )
  {
    super( message, throwable );
  }

}
