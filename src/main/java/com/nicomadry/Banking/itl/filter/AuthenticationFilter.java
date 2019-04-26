package com.nicomadry.Banking.itl.filter;

import com.nicomadry.Banking.api.data.annotation.Authenticated;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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
import java.security.Key;

@Provider
@Authenticated
@Priority(Priorities.AUTHENTICATION)
@ApplicationScoped
public class AuthenticationFilter implements ContainerRequestFilter {

  private Logger logger;

  @Inject
  public void init(Logger logger)
  {
    this.logger = logger;
  }

  @Override
  public void filter(ContainerRequestContext containerRequestContext) throws IOException
  {
    // Get the HTTP Authorization header from the request
    String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader == null) {
      logger.error("#### Received Request without Authorization header. Aborting");
      containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    // Extract the token from the HTTP Authorization header
    String token = authorizationHeader.substring("Bearer".length()).trim();

    try {
      // Validate the token
      Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // TODO: Change with custom signing key
      logger.info("Created Key: " + key.toString());
      Jwts.parser().setSigningKey(key).parseClaimsJws(token);
      logger.info("#### valid token : " + token);
    } catch (Exception e) {
      logger.error("#### invalid token : " + token);
      containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }
  }
}
