package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.data.annotation.SystemZone;
import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.api.service.JwtKeyService;
import com.nicomadry.Banking.itl.util.PasswordUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Date;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
@Path("/auth")
public class AuthenticationEndpoint {

  @Context
  private UriInfo uriInfo;

  private Logger log;

  private EntityManager entityManager;

  private Clock clock;

  private JwtKeyService jwtKeyService;

  @Inject
  public void init(Logger log, EntityManager entityManager, @SystemZone Clock clock, JwtKeyService jwtKeyService)
  {
    this.log = log;
    this.entityManager = entityManager;
    this.clock = clock;
    this.jwtKeyService = jwtKeyService;
  }

  @POST
  @Path("/login")
  @Consumes(APPLICATION_JSON)
  @Produces(APPLICATION_JSON)
  public Response authenticateUser(UserDTO userDTO)
  {
    try {
      final String username = userDTO.getUsername();
      final String password = userDTO.getPassword();

      // Authenticate the user using the credentials provided
      authenticate(username, password);

      // Issue a token for the user
      String token = issueToken(username);

      // Return the token on the response
      return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();
    } catch (Exception e) {
      log.error("Login failed with Reason: " + e.getMessage(), e);
      return Response.status(UNAUTHORIZED).build();
    }
  }

  private void authenticate(String username, String password) throws Exception
  {
    TypedQuery<User> query = entityManager.createNamedQuery(User.FIND_BY_NAME, User.class);
    query.setParameter("username", username);

    User user = query.getSingleResult();

    if (user == null) {
      log.error("User for [" + username + "] not found");
      throw new SecurityException("Invalid user/password");
    }

    if (!PasswordUtils.checkPassword(password, user.getPassword())) {
      log.error("Password not valid for [" + username + "]");
      throw new SecurityException("Invalid user/password");
    }
  }

  private String issueToken(String username)
  {
    log.debug("Creating key for JWT Token");
    Key key = jwtKeyService.generateKey();

    log.debug("Created JWT Key: " + key.toString());

    final String jwtsToken = Jwts.builder()
            .setSubject(username)
            .setIssuer(uriInfo.getAbsolutePath().toString())
            .setIssuedAt(new Date())
            .setExpiration(createExpirationDate())
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

    log.debug("Created jwts token: " + jwtsToken);

    return jwtsToken;
  }

  private Date createExpirationDate()
  {
    final ZonedDateTime expirationDate = ZonedDateTime.now(clock).plusMinutes(15);
    return Date.from(expirationDate.toInstant());
  }

}
