package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.data.annotation.Authenticated;
import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.dto.CurrencyDTO;
import com.nicomadry.Banking.api.data.entity.Balance;
import com.nicomadry.Banking.api.data.enums.BalanceType;
import com.nicomadry.Banking.api.service.BalanceService;
import com.nicomadry.Banking.api.service.CurrencyService;
import com.nicomadry.Banking.itl.exception.DeserializationException;
import com.nicomadry.Banking.itl.exception.HTTPRequestException;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.ValidationException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.nicomadry.Banking.api.rest.BalanceEndpoint.BALANCE_API_URI;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path( BALANCE_API_URI )
@ApplicationScoped
@Transactional( value = REQUIRED, rollbackOn = Exception.class )
public class BalanceEndpoint {

  static final String BALANCE_API_URI = "/balance";

  private static final String LIST_CURRENCIES_URI = "/listCurrencies";

  // TODO: The user id should not be a part of the request but should be validated using the JWT ( LATER_VERSION )
  private static final String GET_CURRENT_BALANCE_URI = "/currentBalance/{accountId: [0-9]}/{userId: [0-9]}";


  private CurrencyService currencyService;
  private BalanceService balanceService;

  @Inject
  public void init( CurrencyService currencyService, BalanceService balanceService )
  {
    this.currencyService = currencyService;
    this.balanceService = balanceService;
  }

  @GET
  @Authenticated
  @Path( LIST_CURRENCIES_URI )
  @Produces( APPLICATION_JSON )
  public Response listCurrencies() throws HTTPRequestException, DeserializationException
  {
    // TESTED AND WORKING
    final List<CurrencyDTO> currencies = currencyService.getAllCurrencies();

    return Response.ok( currencies ).build();
  }

  @GET
  @Authenticated
  @Path( GET_CURRENT_BALANCE_URI )
  @Produces( APPLICATION_JSON )
  public Response getCurrentBalance( @PathParam( "accountId" ) Long accountId,
                                     @PathParam( "userId" ) Long userId ) throws NotFoundException, ValidationException
  {
    // TESTED AND WORKING
    final Optional<Balance> balanceOptional = balanceService.getCurrentBalance( accountId, userId );
    final BalanceDTO currentBalanceDto = balanceOptional.map( BalanceDTO::new ).orElseGet( this::createEmptyBalance );

    return Response.ok( currentBalanceDto ).build();
  }

  /**
   * Creates an completely empty {@link BalanceDTO} to signal an account without any balance
   *
   * @return the empty {@link BalanceDTO}
   */
  private BalanceDTO createEmptyBalance()
  {
    return new BalanceDTO( 0L, BigDecimal.ZERO, "", BalanceType.AVAILABLE );
  }

}
