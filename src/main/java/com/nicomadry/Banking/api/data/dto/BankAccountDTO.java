package com.nicomadry.Banking.api.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicomadry.Banking.api.data.entity.BankAccount;

import java.util.List;
import java.util.stream.Collectors;

public class BankAccountDTO extends IdentifiableDTO {

  private String accountNumber;

  private List<UserDTO> users;

  private String accountName;

  private Boolean blocked;

  private String accountCurrency;

  @JsonCreator
  public BankAccountDTO( @JsonProperty( "accountNumber" ) String accountNumber, @JsonProperty( "accountName" ) String accountName,
                         @JsonProperty( "accountCurrency" ) String accountCurrency,
                         @JsonProperty( "blocked" ) Boolean blocked )
  {
    this.accountNumber = accountNumber;
    this.accountName = accountName;
    this.accountCurrency = accountCurrency;
    this.blocked = blocked != null ? blocked : false;
  }

  public BankAccountDTO( BankAccount bankAccount )
  {
    super( bankAccount );
    this.accountNumber = bankAccount.getAccountNumber();
    this.accountName = bankAccount.getAccountName();
    this.blocked = bankAccount.getBlocked();
    this.accountCurrency = bankAccount.getAccountCurrency();
    this.users = bankAccount.getUsers().stream().map( UserDTO::new ).collect( Collectors.toList() );
  }

  public String getAccountNumber()
  {
    return accountNumber;
  }

  public void setAccountNumber( String accountNumber )
  {
    this.accountNumber = accountNumber;
  }

  public List<UserDTO> getUsers()
  {
    return users;
  }

  public void setUsers( List<UserDTO> users )
  {
    this.users = users;
  }

  public String getAccountName()
  {
    return accountName;
  }

  public void setAccountName( String accountName )
  {
    this.accountName = accountName;
  }

  public Boolean getBlocked()
  {
    return blocked;
  }

  public void setBlocked( Boolean blocked )
  {
    this.blocked = blocked;
  }

  public String getAccountCurrency()
  {
    return accountCurrency;
  }

  public void setAccountCurrency( String accountCurrency )
  {
    this.accountCurrency = accountCurrency;
  }
}
