package com.nicomadry.Banking.itl.exception;

/**
 * This exception is thrown by actions performing entity deletions. ( {@link javax.persistence.EntityManager#remove(Object)} )
 */
public class NotDeletableException extends Exception {

  public NotDeletableException( String message )
  {
    super( message );
  }

  public NotDeletableException( String message, Throwable throwable )
  {
    super( message, throwable );
  }

}
