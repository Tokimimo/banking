package com.nicomadry.Banking.api.service;

import com.nicomadry.Banking.itl.exception.DeserializationException;
import com.nicomadry.Banking.itl.exception.HTTPRequestException;
import com.nicomadry.Banking.itl.exception.ValidationException;

/**
 * The IBAN servie encapsulates the 3rd party API to retrieve IBAN information's in a service to to make the API interchangeable.
 */
public interface IBANService {

  /**
   * Performs a check for the blacklist of IBAN's and returns the blacklist status of the given IBAN.
   *
   * @param iban The IBAN to check for the blacklist status
   *
   * @return True, if the IBAN is blacklisted, false otherwise
   */
  boolean isIbanBlacklisted( String iban );

  /**
   * Performs a check about validity for the given IBAN.
   *
   * @param iban The IBAN to check for validity
   *
   * @return True, if the IBAN is valid, false otherwise
   *
   * @throws HTTPRequestException If the validity of the IBAN couldn't be checked
   */
  boolean isIbanValid( String iban ) throws HTTPRequestException, DeserializationException;

}
