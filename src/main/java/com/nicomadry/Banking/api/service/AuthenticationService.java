package com.nicomadry.Banking.api.service;

import com.nicomadry.Banking.api.data.dto.UserDTO;

import javax.ws.rs.core.UriInfo;

/**
 * The authentication service is only responsible for creating and destroying sessions.
 */
public interface AuthenticationService {

  /**
   * Performs the authentication and login of the given {@link UserDTO User} resulting in a JWT token. The session is active for 15 minutes
   * and currently isn't renewed upon activity.
   *
   * @param userDTO The {@link UserDTO User} trying to authenticate
   * @param uriInfo The {@link UriInfo} providing information's of the authentication request
   *
   * @return The JWT Token already encoded as Base64 String.
   *
   * @throws SecurityException If the authentication fails
   */
  String login( UserDTO userDTO, UriInfo uriInfo ) throws SecurityException;

  /**
   * Performs the de-authentication of the given {@link UserDTO User}. This directly invalidates the session and JWT token.
   *
   * @param token   Tbe token of the active session, which needs to be invalidated
   * @param uriInfo The {@link UriInfo} providing information's of the authentication request
   *
   * @return The invalid JWT Token already encoded as Base64 String.
   *
   * @throws SecurityException If the de-authentication fails
   */
  String logout( String token, UriInfo uriInfo ) throws SecurityException;


}
