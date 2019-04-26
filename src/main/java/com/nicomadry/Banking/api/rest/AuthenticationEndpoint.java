package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.data.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
@Path("/auth")
public class AuthenticationEndpoint {

  @Context
  private UriInfo uriInfo;

  private Logger log;

  private EntityManager entityManager;

  @Inject
  public void init(Logger log, EntityManager entityManager)
  {
    this.log = log;
    this.entityManager = entityManager;
  }

  @POST
  @Path("/login")
  @Consumes(APPLICATION_FORM_URLENCODED)
  public Response authenticateUser(@FormParam("login") String login,
                                   @FormParam("password") String password)
  {
    try {
      // Authenticate the user using the credentials provided
      authenticate(login, password);

      // Issue a token for the user
      String token = issueToken(login);

      // Return the token on the response
      return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();
    } catch (Exception e) {
      return Response.status(UNAUTHORIZED).build();
    }
  }

  private void authenticate(String username, String password) throws Exception
  {
    TypedQuery<User> query = entityManager.createNamedQuery(User.FIND_BY_NAME_AND_PASSWORD, User.class);
    query.setParameter("username", username);
    query.setParameter("password", password);
    User user = query.getSingleResult();

    if (user == null)
      throw new SecurityException("Invalid user/password");
  }

  private String issueToken(String login)
  {
    Key key = Keys.secretKeyFor(SignatureAlgorithm.ES256); // TODO: Replace with application signing key
    return Jwts.builder()
            .setSubject(login)
            .setIssuer(uriInfo.getAbsolutePath().toString())
            .setIssuedAt(new Date())
            .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();
  }

  private Date toDate(LocalDateTime localDateTime)
  {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }

}
