package com.nicomadry.Banking.api.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nicomadry.Banking.itl.deserializer.IbanValidationDeserializer;

@JsonDeserialize( using = IbanValidationDeserializer.class )
public class IbanValidationDTO {

  private String bic;

  private String bankCode;

  private String accountNumber;

  private String countryCode;

  private String iban;

  private boolean valid;

  public IbanValidationDTO()
  {
    super();
  }

  @JsonCreator
  public IbanValidationDTO( @JsonProperty( "iban" ) String iban, @JsonProperty( "bic" ) String bic,
                            @JsonProperty( "bankCode" ) String bankCode, @JsonProperty( "accountNumber" ) String accountNumber,
                            @JsonProperty( "countryCode" ) String countryCode, @JsonProperty( "valid" ) boolean valid )
  {
    this.iban = iban;
    this.bic = bic;
    this.bankCode = bankCode;
    this.accountNumber = accountNumber;
    this.countryCode = countryCode;
    this.valid = valid;
  }

  public String getBic()
  {
    return bic;
  }

  public void setBic( String bic )
  {
    this.bic = bic;
  }

  public String getBankCode()
  {
    return bankCode;
  }

  public void setBankCode( String bankCode )
  {
    this.bankCode = bankCode;
  }

  public String getAccountNumber()
  {
    return accountNumber;
  }

  public void setAccountNumber( String accountNumber )
  {
    this.accountNumber = accountNumber;
  }

  public String getCountryCode()
  {
    return countryCode;
  }

  public void setCountryCode( String countryCode )
  {
    this.countryCode = countryCode;
  }

  public String getIban()
  {
    return iban;
  }

  public void setIban( String iban )
  {
    this.iban = iban;
  }

  public boolean isValid()
  {
    return valid;
  }

  public void setValid( boolean valid )
  {
    this.valid = valid;
  }
}
