package com.nicomadry.Banking.api.service;

import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;

/**
 * This service will provide the system with the {@link Key} used to sign and verify JWT tokens. The first generated key will be saved in a
 * static variable and will be returned by every call to {@link JwtKeyService#generateKey()}. Only a call to {@link
 * JwtKeyService#generateNewKey()} will then trigger the generation of a new key. However the old key will be invalidated.
 */
public interface JwtKeyService {

  /**
   * This function will generate a {@link Key} using {@link SignatureAlgorithm#HS512} and store it for further usage.
   *
   * @return The generated {@link Key}
   */
  Key generateKey();

  /**
   * This function will generate a new {@link Key} using {@link SignatureAlgorithm#HS512} and store it for further usage.
   *
   * @return The generated {@link Key}
   */
  Key generateNewKey();

}
