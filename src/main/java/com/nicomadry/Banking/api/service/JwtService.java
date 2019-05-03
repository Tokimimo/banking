package com.nicomadry.Banking.api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Date;
import java.util.Optional;

/**
 * This service wraps use cases for the JWT process and helps in retrieving useful information's.
 */
public interface JwtService {

  /**
   * This function validates the given token as JWT token and retrieves the subject of the token.
   *
   * @param token The JWT token to extract
   *
   * @return An {@link Optional} possibly containing the subject of the token
   */
  Optional<String> retrieveSubject( final String token );

  /**
   * This function validates the given token as JWT token and retrieves the expiration date of the token, which is the expiration of the
   * current session.
   *
   * @param token The JWT token to extract
   *
   * @return An {@link Optional} possibly containing the expiration date of the token
   */
  Optional<Date> getExpirationDate( final String token );

  /**
   * This function validates the given token as JWT token and retrieves the issued date of the token, which is the date the token got
   * generated.
   *
   * @param token The JWT token to extract
   *
   * @return An {@link Optional} possibly containing the creation date of the token
   */
  Optional<Date> getIssuedDate( final String token );

  /**
   * This function retrieves the pure JWT token without the suffix added in the authorization header.
   *
   * @param authorizationHeader The content of the authorization header
   *
   * @return An {@link Optional} possibly containing the extracted JWT token
   */
  Optional<String> getToken( final String authorizationHeader );

  /**
   * This function validates the JWT token and if the token is valid and not expired, exits peacefully. Otherwise an specific exception
   * which describes the problem with the token will be thrown.
   *
   * @param token The JWT token to validate
   *
   * @throws Exception An Exception describing the occurred problem while validating the token
   */
  void validateToken( final String token ) throws Exception;

  /**
   * This function validates the JWT token and returns the {@link Jws} typed to {@link Claims} which means this JWT token is basically a Map
   * containing additional information's.
   *
   * @param token The JWT token to validate and extract
   *
   * @return The {@link Jws} for further processing
   *
   * @throws Exception An Exception describing the occurred problem while validating the token
   */
  Jws<Claims> extractToken( final String token ) throws Exception;

}
