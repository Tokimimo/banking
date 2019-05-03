package com.nicomadry.Banking.api.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class PaymentFilterDTO implements Serializable {

  @NotNull
  private String beneficiaryAccountNumber;

  private ZonedDateTime periodStart;

  @NotNull
  private String periodStartString;

  private ZonedDateTime periodEnd;

  @NotNull
  private String periodEndString;

  public PaymentFilterDTO()
  {
    super();
  }

  @JsonCreator
  public PaymentFilterDTO( @JsonProperty( "iban" ) String beneficiaryAccountNumber,
                           @JsonProperty( "periodStart" ) String periodStartString, @JsonProperty( "periodEnd" ) String periodEndString )
  {
    this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    this.periodStartString = periodStartString;
    this.periodEndString = periodEndString;
  }


  public String getBeneficiaryAccountNumber()
  {
    return beneficiaryAccountNumber;
  }

  public void setBeneficiaryAccountNumber( String beneficiaryAccountNumber )
  {
    this.beneficiaryAccountNumber = beneficiaryAccountNumber;
  }

  public ZonedDateTime getPeriodStart()
  {
    return periodStart;
  }

  public void setPeriodStart( ZonedDateTime periodStart )
  {
    this.periodStart = periodStart;
  }

  public ZonedDateTime getPeriodEnd()
  {
    return periodEnd;
  }

  public void setPeriodEnd( ZonedDateTime periodEnd )
  {
    this.periodEnd = periodEnd;
  }

  public String getPeriodStartString()
  {
    return periodStartString;
  }

  public void setPeriodStartString( String periodStartString )
  {
    this.periodStartString = periodStartString;
  }

  public String getPeriodEndString()
  {
    return periodEndString;
  }

  public void setPeriodEndString( String periodEndString )
  {
    this.periodEndString = periodEndString;
  }
}
