package com.nicomadry.Banking.itl.exception;

/**
 * This exception indicates that the requested resource was not found. <br/> Mostly this will be thrown if an entity wasn't found for the
 * given information's
 */
public class NotFoundException extends Exception {
  public NotFoundException( String message )
  {
    super( message );
  }

  public NotFoundException( String message, Throwable throwable )
  {
    super( message, throwable );
  }
}
