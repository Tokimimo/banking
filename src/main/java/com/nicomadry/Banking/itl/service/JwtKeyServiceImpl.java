package com.nicomadry.Banking.itl.service;

import com.nicomadry.Banking.api.service.JwtKeyService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import java.security.Key;

@ApplicationScoped
@Typed(JwtKeyService.class)
public class JwtKeyServiceImpl implements JwtKeyService {

  private static Key signingKey;

  @Override
  public Key generateKey()
  {
    if (signingKey != null) {
      return signingKey;
    }

    signingKey =  Keys.secretKeyFor(SignatureAlgorithm.HS512);

    return signingKey;
  }

  @Override
  public Key generateNewKey()
  {
    signingKey =  Keys.secretKeyFor(SignatureAlgorithm.HS512);

    return signingKey;
  }
}
