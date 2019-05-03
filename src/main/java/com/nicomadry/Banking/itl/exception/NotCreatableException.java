package com.nicomadry.Banking.itl.exception;

/**
 * This exception is thrown by actions performing entity creation. ( {@link javax.persistence.EntityManager#persist(Object)} )
 */
public class NotCreatableException extends Exception {

  public NotCreatableException( String message )
  {
    super( message );
  }

  public NotCreatableException( String message, Throwable throwable )
  {
    super( message, throwable );
  }

}
