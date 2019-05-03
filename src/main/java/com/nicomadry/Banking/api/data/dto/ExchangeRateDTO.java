package com.nicomadry.Banking.api.data.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nicomadry.Banking.itl.deserializer.ExchangeRateDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonDeserialize( using = ExchangeRateDeserializer.class )
public class ExchangeRateDTO implements Serializable {

  private String exchange;

  private String currencyFrom;

  private String currencyTo;

  private BigDecimal rate;

  public ExchangeRateDTO()
  {

  }

  public String getExchange()
  {
    return exchange;
  }

  public void setExchange( String exchange )
  {
    this.exchange = exchange;
  }

  public BigDecimal getRate()
  {
    return rate;
  }

  public void setRate( BigDecimal rate )
  {
    this.rate = rate;
  }

  public String getCurrencyFrom()
  {
    return currencyFrom;
  }

  public void setCurrencyFrom( String currencyFrom )
  {
    this.currencyFrom = currencyFrom;
  }

  public String getCurrencyTo()
  {
    return currencyTo;
  }

  public void setCurrencyTo( String currencyTo )
  {
    this.currencyTo = currencyTo;
  }
}
