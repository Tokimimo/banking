package com.nicomadry.Banking.api.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CurrencyDTO implements Serializable {

  private String currencyName;
  private String currencySymbol;
  private String currencyId;


  @JsonCreator
  public CurrencyDTO( @JsonProperty( "currencyName" ) String currencyName, @JsonProperty( "currencySymbol" ) String currencySymbol,
                      @JsonProperty( "id" ) String currencyId )
  {
    this.currencyId = currencyId;
    this.currencyName = currencyName;
    this.currencySymbol = currencySymbol;
  }

  public String getCurrencyName()
  {
    return currencyName;
  }

  public void setCurrencyName( String currencyName )
  {
    this.currencyName = currencyName;
  }

  public String getCurrencySymbol()
  {
    return currencySymbol;
  }

  public void setCurrencySymbol( String currencySymbol )
  {
    this.currencySymbol = currencySymbol;
  }

  public String getCurrencyId()
  {
    return currencyId;
  }

  public void setCurrencyId( String currencyId )
  {
    this.currencyId = currencyId;
  }
}
