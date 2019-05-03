package com.nicomadry.Banking.itl.service;

import com.nicomadry.Banking.api.service.JwtKeyService;
import com.nicomadry.Banking.api.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Dependent
@Typed( JwtService.class )
public class JwtServiceImpl implements JwtService {

  private JwtKeyService jwtKeyService;

  @Inject
  public void init( JwtKeyService jwtKeyService )
  {
    this.jwtKeyService = jwtKeyService;
  }

  @Override
  public Optional<String> retrieveSubject( String token )
  {
    Key key = jwtKeyService.generateKey();

    try {
      final String subject = Jwts.parser().setSigningKey( key ).parseClaimsJws( token ).getBody().getSubject();
      return Optional.of( subject );
    }
    catch ( Exception e ) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Date> getExpirationDate( String token )
  {
    Key key = jwtKeyService.generateKey();

    try {
      final Date expiration = Jwts.parser().setSigningKey( key ).parseClaimsJws( token ).getBody().getExpiration();
      return Optional.of( expiration );
    }
    catch ( Exception e ) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Date> getIssuedDate( String token )
  {
    Key key = jwtKeyService.generateKey();

    try {
      final Date issuedAt = Jwts.parser().setSigningKey( key ).parseClaimsJws( token ).getBody().getIssuedAt();
      return Optional.of( issuedAt );
    }
    catch ( Exception e ) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> getToken( String authorizationHeader )
  {
    if ( authorizationHeader.startsWith( "Bearer" ) ) {
      return Optional.of( authorizationHeader.substring( "Bearer".length() ).trim() );
    }
    // Bearer is missing in the header, so I can be sure the value wasn't issued by us. It's a forged token
    return Optional.empty();
  }

  @Override
  public void validateToken( String token ) throws Exception
  {
    Key key = jwtKeyService.generateKey();

    final Jws<Claims> claimsJws = Jwts.parser().setSigningKey( key ).parseClaimsJws( token );
  }

  @Override
  public Jws<Claims> extractToken( String token ) throws Exception
  {
    Key key = jwtKeyService.generateKey();

    return Jwts.parser().setSigningKey( key ).parseClaimsJws( token );
  }
}
