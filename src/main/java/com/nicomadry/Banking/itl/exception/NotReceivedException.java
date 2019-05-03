package com.nicomadry.Banking.itl.exception;

/**
 * This exception indicates that a requested resource or information didn't get delivered.
 */
public class NotReceivedException extends Exception {

  public NotReceivedException( String message )
  {
    super( message );
  }

  public NotReceivedException( String message, Throwable throwable )
  {
    super( message, throwable );
  }

}
