package com.nicomadry.Banking.api.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicomadry.Banking.api.data.entity.Payment;
import com.nicomadry.Banking.api.data.enums.PaymentStatus;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PaymentDTO extends IdentifiableDTO {

  @NotNull
  private BigDecimal amount;

  @NotNull
  private String currency;

  @NotNull
  private BankAccountDTO giverAccount;

  @NotNull
  private String beneficiaryAccountNumber;

  @NotNull
  private String beneficiaryName;

  private String communication;

  private ZonedDateTime creationDate;

  private PaymentStatus status;

  public PaymentDTO()
  {
    super();
  }

  @JsonCreator
  public PaymentDTO( @JsonProperty( "amount" ) BigDecimal amount, @JsonProperty( "currency" ) String currency,
                     @JsonProperty( "giverAccount" ) BankAccountDTO giverAccount,
                     @JsonProperty( "beneficiaryAccountNumber" ) String beneficiaryAccountNumber,
                     @JsonProperty( "beneficiaryName" ) String beneficiaryName,
                     @JsonProperty( "communication" ) String communication,
                     @JsonProperty( "creationDate" ) ZonedDateTime creationDate )
  {
    this.amount = amount;
    this.currency = currency;
    this.giverAccount = giverAccount;
    this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    this.beneficiaryName = beneficiaryName;
    this.communication = communication;
    this.creationDate = creationDate;
  }

  public PaymentDTO( Payment payment )
  {
    super( payment );
    this.amount = payment.getAmount();
    this.currency = payment.getCurrency();
    this.giverAccount = payment.getGiverAccount() != null ? new BankAccountDTO( payment.getGiverAccount() ) : null;
    this.beneficiaryAccountNumber = payment.getBeneficiaryAccountNumber();
    this.beneficiaryName = payment.getBeneficiaryName();
    this.communication = payment.getCommunication();
    this.creationDate = payment.getCreationDate();
    this.status = payment.getStatus();
  }

  public BigDecimal getAmount()
  {
    return amount;
  }

  public void setAmount( BigDecimal amount )
  {
    this.amount = amount;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency( String currency )
  {
    this.currency = currency;
  }

  public BankAccountDTO getGiverAccount()
  {
    return giverAccount;
  }

  public void setGiverAccount( BankAccountDTO giverAccount )
  {
    this.giverAccount = giverAccount;
  }

  public String getBeneficiaryAccountNumber()
  {
    return beneficiaryAccountNumber;
  }

  public void setBeneficiaryAccountNumber( String beneficiaryAccountNumber )
  {
    this.beneficiaryAccountNumber = beneficiaryAccountNumber;
  }

  public String getBeneficiaryName()
  {
    return beneficiaryName;
  }

  public void setBeneficiaryName( String beneficiaryName )
  {
    this.beneficiaryName = beneficiaryName;
  }

  public String getCommunication()
  {
    return communication;
  }

  public void setCommunication( String communication )
  {
    this.communication = communication;
  }

  public ZonedDateTime getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate( ZonedDateTime creationDate )
  {
    this.creationDate = creationDate;
  }

  public PaymentStatus getStatus()
  {
    return status;
  }

  public void setStatus( PaymentStatus status )
  {
    this.status = status;
  }
}
