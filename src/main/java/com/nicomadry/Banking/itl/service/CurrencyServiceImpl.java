package com.nicomadry.Banking.itl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.dto.CurrencyDTO;
import com.nicomadry.Banking.api.data.dto.CurrencyListDTO;
import com.nicomadry.Banking.api.data.dto.ExchangeRateDTO;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.service.CurrencyService;
import com.nicomadry.Banking.itl.exception.DeserializationException;
import com.nicomadry.Banking.itl.exception.HTTPRequestException;
import com.nicomadry.Banking.itl.exception.NotReceivedException;
import com.nicomadry.Banking.itl.exception.ValidationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.nicomadry.Banking.itl.util.RequestUtils.getResponseContent;
import static java.util.Objects.requireNonNull;

@Dependent
@Typed( CurrencyService.class )
public class CurrencyServiceImpl implements CurrencyService {

  // TODO: This is very bad and should be replaced with a property in a property file ( LATER_VERSION )
  private static final String API_KEY = "XXXXXXXXX";

  // REMARK: This could also be done more dynamically but for the sake of the demo it is hardcoded
  private static final String BASE_URI = "http://free.currconv.com/api/v7";

  private static final String CONVERT_URI = "/convert?q={FROM_CURRENCY}_{TO_CURRENCY}&compact=ultra&apiKey=" + API_KEY;

  private static final String LIST_CURRENCIES_URI = "/currencies?apiKey=" + API_KEY;

  private Logger log;

  @Inject
  public void init( Logger log )
  {
    this.log = log;
  }

  @Override
  public List<CurrencyDTO> getAllCurrencies() throws HTTPRequestException, DeserializationException
  {
    final Optional<CurrencyListDTO> currencyListDTO = requestAllCurrencies();

    if ( currencyListDTO.isPresent() ) {
      final Map<String, CurrencyDTO> currencies = currencyListDTO.get().getResults();

      final Collection<CurrencyDTO> currencyList = currencies.values();
      return new ArrayList<>( currencyList );
    }

    return Collections.emptyList();
  }

  /**
   * Builds and performs a request to retrieve a list of all currencies.
   *
   * @return The list of currencies
   *
   * @throws HTTPRequestException     If requesting the currencies failed
   * @throws DeserializationException If the list of currencies couldn't be parsed
   */
  private Optional<CurrencyListDTO> requestAllCurrencies() throws HTTPRequestException, DeserializationException
  {
    final String currencyRequest = BASE_URI + LIST_CURRENCIES_URI;
    log.debug( "Build URL: " + currencyRequest );

    final HttpGet request = new HttpGet( currencyRequest );

    HttpResponse response = performRequest( currencyRequest, request );

    final Optional<String> responseContent = getResponseContent( response );

    if ( responseContent.isPresent() ) {
      return Optional.of( convertResponseToPojo( responseContent.get(), CurrencyListDTO.class ) );
    }

    return Optional.empty();
  }

  /**
   * Performs the given {@link HttpGet request}.
   *
   * @param requestUrl The request URL, the request will be directed to ( Logging )
   * @param request    The {@link HttpGet request} to perform
   *
   * @return The received {@link HttpResponse response}
   *
   * @throws HTTPRequestException If the request failed
   */
  private HttpResponse performRequest( String requestUrl, HttpGet request ) throws HTTPRequestException
  {
    final HttpClient client = HttpClientBuilder.create().build();

    try {
      return client.execute( request );
    }
    catch ( IOException e ) {
      log.error( "Error performing request to URL: " + requestUrl, e );
      throw new HTTPRequestException( "Error perform request to URL: " + requestUrl );
    }
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

  @Override
  public boolean isValidCurrency( String currency ) throws HTTPRequestException, DeserializationException
  {
    final Optional<CurrencyListDTO> currencyListDTO = requestAllCurrencies();

    return currencyListDTO.map( currencyList -> currencyList.getResults().containsKey( currency ) ).orElse( false );
  }

  @Override
  public void checkCurrencyValidity( String currency ) throws ValidationException
  {
    try {
      final boolean isValid = isValidCurrency( currency );

      if ( !isValid ) {
        throw new ValidationException( "The currency: " + currency + " is not a valid currency" );
      }
    }
    catch ( HTTPRequestException | DeserializationException e ) {
      log.error( "Couldn't verify currency: " + currency, e );
      throw new ValidationException( "Couldn't verify currency: " + currency, e );
    }
  }

  @Override
  public Optional<ExchangeRateDTO> getExchangeRate( String currencyFrom,
                                                    String currencyTo ) throws HTTPRequestException, DeserializationException
  {
    final String convertURI = CONVERT_URI.replace( "{FROM_CURRENCY}", currencyFrom ).replace( "{TO_CURRENCY}", currencyTo );
    final String requestUrl = BASE_URI + convertURI;

    log.info( "Build URL: " + requestUrl );

    final HttpGet request = new HttpGet( requestUrl );

    HttpResponse response = performRequest( requestUrl, request );

    final Optional<String> responseContent = getResponseContent( response );

    if ( responseContent.isPresent() ) {
      log.info( responseContent.get() );
      return Optional.of( convertResponseToPojo( responseContent.get(), ExchangeRateDTO.class ) );
    }

    return Optional.empty();
  }

  @Override
  public Optional<BigDecimal> getExchangedMoney( String currencyFrom, String currencyTo, BigDecimal money )
  {
    try {
      final Optional<ExchangeRateDTO> exchangeRateOptional = getExchangeRate( currencyFrom, currencyTo );

      final ExchangeRateDTO exchangeRateDTO = exchangeRateOptional
              .orElseThrow( () -> new NotReceivedException( "Didn't receive the exchange to perform the conversion" ) );

      final BigDecimal exchangeRate = exchangeRateDTO.getRate();
      return Optional.of( exchangeRate.multiply( money ) );
    }
    catch ( HTTPRequestException | NotReceivedException | DeserializationException e ) {
      return Optional.empty();
    }
  }

  @Override
  public BigDecimal getCorrectMoneyAmount( BankAccount bankAccount, BalanceDTO balanceDTO ) throws ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( balanceDTO, "The balanceDTO must not be null" );

    BigDecimal correctAmount;

    if ( !bankAccount.getAccountCurrency().equals( balanceDTO.getCurrency() ) ) {
      correctAmount = getExchangedMoney( balanceDTO.getCurrency(), bankAccount.getAccountCurrency(), balanceDTO.getAmount() )
              .orElseThrow( () -> new ValidationException( "The money couldn't be exchanged to the new currency" ) );
    }
    else {
      correctAmount = balanceDTO.getAmount();
    }

    return correctAmount;
  }

}
