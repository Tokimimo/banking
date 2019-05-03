package com.nicomadry.Banking.itl.filter;

import com.nicomadry.Banking.api.data.annotation.Authenticated;
import com.nicomadry.Banking.api.service.JwtService;
import org.jboss.logging.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Optional;

@Provider
@Authenticated
@Priority( Priorities.AUTHENTICATION )
@ApplicationScoped
public class AuthenticationFilter implements ContainerRequestFilter {

  private Logger logger;

  private JwtService jwtService;

  @Inject
  public void init( Logger logger, JwtService jwtService )
  {
    this.logger = logger;
    this.jwtService = jwtService;
  }

  @Override
  public void filter( ContainerRequestContext containerRequestContext ) throws IOException
  {
    // Get the HTTP Authorization header from the request
    String authorizationHeader = containerRequestContext.getHeaderString( HttpHeaders.AUTHORIZATION );

    if ( authorizationHeader == null ) {
      logger.error( "#### Received Request without Authorization header. Aborting" );
      containerRequestContext.abortWith( Response.status( Response.Status.UNAUTHORIZED ).build() );
    }

    // Extract the token from the HTTP Authorization header
    Optional<String> serviceToken = jwtService.getToken( authorizationHeader );

    if ( !serviceToken.isPresent() ) {
      containerRequestContext.abortWith( Response.status( Response.Status.UNAUTHORIZED ).build() );
    }

    final String token = serviceToken.get();

    logger.debug( "Recieved Token: " + token );

    try {
      // Validate the token
      jwtService.validateToken( token );

      final Optional<String> subject = jwtService.retrieveSubject( token );
      final String username = subject.orElseThrow( () -> new SecurityException( "Invalid token" ) );

      logger.debug( "#### valid token for user: " + username );
    }
    catch ( Exception e ) {
      logger.error( "#### invalid token : " + token );
      containerRequestContext.abortWith( Response.status( Response.Status.UNAUTHORIZED ).build() );
    }
  }
}
