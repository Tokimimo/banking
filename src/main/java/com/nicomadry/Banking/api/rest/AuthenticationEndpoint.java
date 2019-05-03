package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.data.annotation.Authenticated;
import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.service.AuthenticationService;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static com.nicomadry.Banking.api.rest.AuthenticationEndpoint.BASE_API_URI;
import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
@Path( BASE_API_URI )
public class AuthenticationEndpoint {

  static final String BASE_API_URI = "/auth";
  private static final String LOGIN_URI = "/login";
  private static final String LOGOUT_URI = "/logout";

  private static final String BEARER = "Bearer ";

  @Context
  private UriInfo uriInfo;

  private Logger log;

  private AuthenticationService authenticationService;

  @Inject
  public void init( Logger log, AuthenticationService authenticationService )
  {
    this.log = log;
    this.authenticationService = authenticationService;
  }

  @POST
  @Path( LOGIN_URI )
  @Consumes( APPLICATION_JSON )
  @Produces( APPLICATION_JSON )
  public Response login( UserDTO userDTO ) throws SecurityException
  {
    requireNonNull(userDTO, "The userDTO must not be null");
    // TESTED AND WORKING
    try {
      String token = authenticationService.login( userDTO, uriInfo );

      return Response.ok().header( AUTHORIZATION, BEARER + token ).build();
    }
    catch ( Exception e ) {
      log.error( "Login failed with Reason: " + e.getMessage(), e );
      return Response.status( UNAUTHORIZED ).build();
    }
  }

  @POST
  @Path( LOGOUT_URI )
  @Consumes( APPLICATION_JSON )
  @Produces( APPLICATION_JSON )
  @Authenticated
  public Response logout( HttpServletRequest httpServletRequest ) throws SecurityException
  {
    requireNonNull(httpServletRequest, "The httpServletRequest must not be null");

    final String authenticationHeader = httpServletRequest.getHeader( AUTHORIZATION );

    try {
      String token = authenticationService.logout( authenticationHeader, uriInfo );

      return Response.ok().header( AUTHORIZATION, "Bearer " + token ).build();
    }
    catch ( Exception e ) {
      log.error( "Logout failed with Reason: " + e.getMessage(), e );
      return Response.status( UNAUTHORIZED ).build();
    }
  }

}
