package com.nicomadry.Banking.itl.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * This Exception is thrown by custom {@link JsonDeserializer} implementations.
 */
public class DeserializationException extends JsonProcessingException {

  public DeserializationException( String msg )
  {
    super( msg );
  }

  public DeserializationException( String msg, Throwable rootCause )
  {
    super( msg, rootCause );
  }

  public DeserializationException( Throwable rootCause )
  {
    super( rootCause );
  }
}
