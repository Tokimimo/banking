package com.nicomadry.Banking.itl.exception;

/**
 * This Exception is thrown by actions performing HTTP requests.
 */
public class HTTPRequestException extends Exception {

  public HTTPRequestException( String message )
  {
    super( message );
  }

  public HTTPRequestException( String message, Throwable throwable )
  {
    super( message, throwable );
  }

}
