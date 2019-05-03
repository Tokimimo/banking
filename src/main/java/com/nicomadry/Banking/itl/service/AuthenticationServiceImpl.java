package com.nicomadry.Banking.itl.service;

import com.nicomadry.Banking.api.data.annotation.SystemZone;
import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.api.service.AuthenticationService;
import com.nicomadry.Banking.api.service.JwtKeyService;
import com.nicomadry.Banking.api.service.JwtService;
import com.nicomadry.Banking.itl.util.PasswordUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Dependent
@Typed( AuthenticationService.class )
public class AuthenticationServiceImpl implements AuthenticationService {

  private Logger log;
  private JwtKeyService jwtKeyService;
  private EntityManager entityManager;
  private Clock clock;
  private JwtService jwtService;

  @Inject
  public void init( Logger log, JwtKeyService jwtKeyService, EntityManager entityManager, @SystemZone Clock clock, JwtService jwtService )
  {
    this.log = log;
    this.jwtKeyService = jwtKeyService;
    this.entityManager = entityManager;
    this.clock = clock;
    this.jwtService = jwtService;
  }

  @Override
  public String login( UserDTO userDTO, UriInfo uriInfo ) throws SecurityException
  {
    requireNonNull( userDTO, "The userDTO must not be null" );
    requireNonNull( uriInfo, "The uriInfo must not be null" );

    final String username = userDTO.getUsername();
    final String password = userDTO.getPassword();

    // Authenticate the user using the credentials provided
    authenticate( username, password );

    // Issue a token for the user
    return issueToken( username, uriInfo );
  }

  /**
   * Performs the simple authentication, checking if the {@link User} exists for the given username and then if the correct password for the
   * user was entered.
   *
   * @param username The username of the {@link User} trying to authenticate
   * @param password The password of the {@link User} trying to authenticate
   *
   * @throws SecurityException If the {@link User} doesn't exist or if the password was wrong
   */
  private void authenticate( String username, String password ) throws SecurityException
  {
    TypedQuery<User> query = entityManager.createNamedQuery( User.FIND_BY_NAME, User.class );
    query.setParameter( "username", username );

    User user = query.getSingleResult();

    if ( user == null ) {
      log.error( "User for [" + username + "] not found" );
      throw new SecurityException( "Invalid user/password" );
    }

    if ( !PasswordUtils.checkPassword( password, user.getPassword() ) ) {
      log.error( "Password not valid for [" + username + "]" );
      throw new SecurityException( "Invalid user/password" );
    }
  }

  /**
   * Creates a JWT token issued to the authenticated {@link User}, acting as proof of authentication.
   *
   * @param username The username of the authenticated {@link User}
   * @param uriInfo  The {@link UriInfo} providing information's for the authentication
   *
   * @return The Base64 encoded JWT token
   */
  private String issueToken( String username, UriInfo uriInfo )
  {
    log.debug( "Creating key for JWT Token" );
    Key key = jwtKeyService.generateKey();

    log.debug( "Created JWT Key: " + key.toString() );

    final String jwtsToken = Jwts.builder()
                                 .setSubject( username )
                                 .setIssuer( uriInfo.getAbsolutePath().toString() )
                                 .setIssuedAt( new Date() )
                                 .setExpiration( createExpirationDate() )
                                 .signWith( key, SignatureAlgorithm.HS512 )
                                 .compact();

    log.debug( "Created jwts token: " + jwtsToken );

    return jwtsToken;
  }

  /**
   * Creates a {@link Date} set 15 minutes to the future of now, which will be used as session duration.
   *
   * @return The created {@link Date} for the session duration
   */
  private Date createExpirationDate()
  {
    final ZonedDateTime expirationDate = ZonedDateTime.now( clock ).plusMinutes( 15 );
    return Date.from( expirationDate.toInstant() );
  }

  @Override
  public String logout( String authenticationHeader, UriInfo uriInfo ) throws SecurityException
  {
    requireNonNull( authenticationHeader, "The authenticationHeader must not be null" );
    requireNonNull( uriInfo, "The uriInfo must not be null" );

    final Optional<String> token = jwtService.getToken( authenticationHeader );

    if ( !token.isPresent() ) {
      throw new SecurityException( "Couldn't retrieve JWT token" );
    }

    try {
      final Key key = jwtKeyService.generateKey();
      final Jws<Claims> tokenClaims = jwtService.extractToken( token.get() );
      final String username = tokenClaims.getBody().getSubject();
      final Date issuedDate = tokenClaims.getBody().getIssuedAt();

      return Jwts.builder()
                 .setSubject( username )
                 .setIssuer( uriInfo.getAbsolutePath().toString() )
                 .setIssuedAt( issuedDate )
                 .setExpiration( createInvalidExpirationDate() )
                 .signWith( key, SignatureAlgorithm.HS512 )
                 .compact();

    }
    catch ( Exception e ) {
      log.error( "Logout failed with Reason: " + e.getMessage(), e );
      throw new SecurityException( "Error during logout", e );
    }
  }

  /**
   * Creates a {@link Date} set 15 hours to the past of now, which will be used to invalidate the session token.
   *
   * @return The created {@link Date} used to invalidate the session
   */
  private Date createInvalidExpirationDate()
  {
    final ZonedDateTime expirationDate = ZonedDateTime.now( clock ).minusHours( 15 );
    return Date.from( expirationDate.toInstant() );
  }
}
