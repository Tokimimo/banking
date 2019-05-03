package com.nicomadry.Banking.api.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicomadry.Banking.api.data.entity.Balance;
import com.nicomadry.Banking.api.data.enums.BalanceType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BalanceDTO extends IdentifiableDTO {

  @NotNull
  private BigDecimal amount;

  @NotNull
  private String currency;

  private BalanceType type;

  private BankAccountDTO bankAccountDTO;

  public BalanceDTO()
  {
    super();
  }

  @JsonCreator
  public BalanceDTO( @JsonProperty( "balanceId" ) Long balanceId, @JsonProperty( "amount" ) BigDecimal amount,
                     @JsonProperty( "currency" ) String currency,
                     @JsonProperty( "type" ) BalanceType type )
  {
    this.id = balanceId;
    this.amount = amount;
    this.currency = currency;
    this.type = type;
  }

  public BalanceDTO( Balance balance )
  {
    super( balance );
    this.amount = balance.getAmount();
    this.currency = balance.getCurrency();
    this.type = balance.getType();
    this.bankAccountDTO = new BankAccountDTO( balance.getBankAccount() );
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

  public BalanceType getType()
  {
    return type;
  }

  public void setType( BalanceType type )
  {
    this.type = type;
  }

  @JsonIgnore
  public BankAccountDTO getBankAccountDTO()
  {
    return bankAccountDTO;
  }

  public void setBankAccountDTO( BankAccountDTO bankAccountDTO )
  {
    this.bankAccountDTO = bankAccountDTO;
  }
}
