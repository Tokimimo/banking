package com.nicomadry.Banking.itl.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * This utils class holds helper methods to handle HTTP requests and responses.
 */
public class RequestUtils {

  /**
   * Private Constructor
   */
  private RequestUtils()
  {
    super();
  }

  /**
   * Extracts the {@link HttpEntity response content} from the {@link HttpResponse}.
   *
   * @param response The received {@link HttpResponse response} of a request
   *
   * @return The {@link Optional}, possibly containing the extracted content
   */
  public static Optional<String> getResponseContent( HttpResponse response )
  {
    final Logger log = Logger.getLogger( RequestUtils.class.getName() );

    String responseContent;

    try ( BufferedReader rd = new BufferedReader(
            new InputStreamReader( response.getEntity().getContent() ) ) )
    {
      StringBuilder result = new StringBuilder();
      String line;

      while ( (line = rd.readLine()) != null ) {
        result.append( line );
      }

      responseContent = result.toString();
      log.debug( "Received http response content: " + responseContent );
    }
    catch ( IOException e ) {
      log.error( "Error during response processing", e );
      return Optional.empty();
    }

    return Optional.of( responseContent );
  }

}
