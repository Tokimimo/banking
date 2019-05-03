package com.nicomadry.Banking.itl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicomadry.Banking.api.data.dto.CurrencyListDTO;
import com.nicomadry.Banking.api.data.dto.IbanValidationDTO;
import com.nicomadry.Banking.api.service.IBANService;
import com.nicomadry.Banking.itl.exception.DeserializationException;
import com.nicomadry.Banking.itl.exception.HTTPRequestException;
import com.nicomadry.Banking.itl.exception.ValidationException;
import com.nicomadry.Banking.itl.util.RequestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.nicomadry.Banking.itl.util.RequestUtils.getResponseContent;
import static java.util.Objects.requireNonNull;

@Dependent
@Typed( IBANService.class )
public class IBANServiceImpl implements IBANService {

  // TODO: This is very bad and should be replaced with a property in a property file ( LATER_VERSION )
  private static final String API_KEY = "token XXXXXXXXXXX";

  // REMARK: This could also be done more dynamically but for the sake of the demo it is hardcoded
  private static final String IBAN_CHECK_URI = "https://fintechtoolbox.com/validate/iban.json?iban=";

  private static final String AUTHORIZATION_HEADER = "Authorization";

  // TODO: Exchange with proper service to retrieve blacklist ( LATER_VERSION )
  private static final List<String> IBAN_BLACKLIST = new ArrayList<>(
          Arrays.asList( "LU28 0019 4006 4475 0000", "LU12 0010 0012 3456 7891" ) );

  private Logger log;

  @Inject
  public void init( Logger log )
  {
    this.log = log;
  }

  @Override
  public boolean isIbanBlacklisted( String iban )
  {
    requireNonNull( iban, "The iban must not be null" );

    return IBAN_BLACKLIST.contains( iban );
  }

  @Override
  public boolean isIbanValid( String iban ) throws HTTPRequestException, DeserializationException
  {
    requireNonNull( iban, "The iban must not be null" );

    final HttpPost request = createRequest( iban );

    final HttpResponse response = performRequest( request );

    final Optional<String> responseContent = getResponseContent( response );

    if ( responseContent.isPresent() ) {
      final IbanValidationDTO validationDTO = convertResponseToPojo( responseContent.get(), IbanValidationDTO.class );
      validationDTO.setIban( iban );

      return validationDTO.isValid();
    }

    throw new DeserializationException( "Couldn't process the response. IBAN validation failed" );
  }

  /**
   * Converts the received content ( as JSON ) to the POJO equivalent of the given class.
   *
   * @param responseContent The JSON formatted content
   *
   * @return The {@link CurrencyListDTO} containing a list of all currencies
   *
   * @throws DeserializationException If the content cannot be converted to the POJO
   */
  private <T> T convertResponseToPojo( String responseContent, Class<T> resultClass ) throws DeserializationException
  {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      return objectMapper.readValue( responseContent, resultClass );
    }
    catch ( IOException e ) {
      log.error( "Error parsing response as " + resultClass.getName(), e );
      throw new DeserializationException( "The received content is not interpretable as " + resultClass.getName() );
    }
  }

  /**
   * @param iban
   *
   * @return
   */
  private static HttpPost createRequest( String iban )
  {
    final String requestUrl = IBAN_CHECK_URI + iban;
    final HttpPost request = new HttpPost( requestUrl );

    request.addHeader( AUTHORIZATION_HEADER, API_KEY );

    return request;
  }

  /**
   * @param request
   *
   * @return
   *
   * @throws HTTPRequestException
   */
  private HttpResponse performRequest( HttpPost request ) throws HTTPRequestException
  {
    final HttpClient client = HttpClientBuilder.create().build();

    try {
      return client.execute( request );
    }
    catch ( IOException e ) {
      log.error( "Error performing request to URL: " + request.getURI().toString(), e );
      throw new HTTPRequestException( "Error perform request to URL: " + request.getURI().toString() );
    }
  }
}
