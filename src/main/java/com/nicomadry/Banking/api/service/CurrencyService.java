package com.nicomadry.Banking.api.service;

import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.dto.CurrencyDTO;
import com.nicomadry.Banking.api.data.dto.ExchangeRateDTO;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.itl.exception.DeserializationException;
import com.nicomadry.Banking.itl.exception.HTTPRequestException;
import com.nicomadry.Banking.itl.exception.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * The currency service encapsulates the 3rd party API to retrieve exchange rates and currency information's in a service to make the API
 * interchangeable.
 */
public interface CurrencyService {

  /**
   * Performs a request to <b>currconv.com</b> to retrieve a list of all currencies containing the short name, full name and optionally a
   * symbol.
   *
   * @return The list of {@link CurrencyDTO currencies}
   *
   * @throws DeserializationException If the response of the 3rd party api couldn't be parsed
   * @throws HTTPRequestException     If the request to the 3rd party api couldn't be performed
   */
  List<CurrencyDTO> getAllCurrencies() throws HTTPRequestException, DeserializationException;

  /**
   * Performs a request to <b>currconv.com</b> to retrieve a list of all currencies and checks if the given currency is valid.
   *
   * @param currency The short name of the currency (e.g: EUR,USD )
   *
   * @return True, if the currency is valid, false otherwise
   *
   * @throws DeserializationException If the response of the 3rd party api couldn't be parsed
   * @throws HTTPRequestException     If the request to the 3rd party api couldn't be performed
   */
  boolean isValidCurrency( String currency ) throws HTTPRequestException, DeserializationException;

  /**
   * Checks if the currency is valid.
   *
   * @param currency The currency to validated
   *
   * @throws ValidationException If the currency is not valid or couldn't be validated
   */
  void checkCurrencyValidity( String currency ) throws ValidationException;

  /**
   * Performs a request to <b>currconv.com</b> to retrieve the current exchange rate.
   *
   * @param currencyFrom The currency to originate from
   * @param currencyTo   The currency to exchange to
   *
   * @return The {@link BigDecimal} representing the exchange rate
   *
   * @throws HTTPRequestException     If the request to the 3rd party api couldn't be performed
   * @throws DeserializationException If the response of the 3rd party api couldn't be parsed
   */
  Optional<ExchangeRateDTO> getExchangeRate( String currencyFrom, String currencyTo ) throws HTTPRequestException, DeserializationException;

  /**
   * Retrieves the exchange rate for the two balances and returns the new amount of money in the new currency.
   *
   * @param currencyFrom The currency the money will be exchanged from
   * @param currencyTo   The currency the money will be exchanged to
   * @param money        The money to be exchanged
   *
   * @return The amount of money after the exchange
   */
  Optional<BigDecimal> getExchangedMoney( String currencyFrom, String currencyTo, BigDecimal money );

  /**
   * Returns the corrected amount of money, if the currency of the transaction differs from the currency of the {@link BankAccount}.
   * Otherwise the amount of the transaction is returned.
   *
   * @param bankAccount The {@link BankAccount} to retrieve the correct currency to convert to
   * @param balanceDTO  The {@link BalanceDTO transaction} containing the amount and currency given
   *
   * @return The correct amount of money in the currency of the account.
   *
   * @throws ValidationException If the money couldn't be exchanged to the correct currency
   */
  BigDecimal getCorrectMoneyAmount( BankAccount bankAccount, BalanceDTO balanceDTO ) throws ValidationException;

}
