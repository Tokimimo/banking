package com.nicomadry.Banking.itl.exception;

/**
 * This exception is thrown by actions performing entity updates. ( {@link javax.persistence.EntityManager#merge(Object)} )
 */
public class NotUpdatableException extends Exception {

  public NotUpdatableException( String message )
  {
    super( message );
  }

  public NotUpdatableException( String message, Throwable throwable )
  {
    super( message, throwable );
  }

}
