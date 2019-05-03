package com.nicomadry.Banking.api.data.entity;

import com.nicomadry.Banking.api.data.dto.BankAccountDTO;
import com.nicomadry.Banking.api.data.model.IdentifiableEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@NamedQueries( {
        @NamedQuery( name = BankAccount.FIND_BY_NAME, query = "SELECT ba from BankAccount ba WHERE ba.accountName = :accountName" ),
        @NamedQuery( name = BankAccount.FIND_BY_NUMBER, query = "SELECT ba from BankAccount ba WHERE ba.accountNumber = :accountNumber" ),
        @NamedQuery( name = BankAccount.FIND_BY_USER, query = "SELECT ba from BankAccount ba INNER JOIN ba.users u WHERE u.id = :userId" ),
        @NamedQuery( name = BankAccount.FIND_ALL, query = "SELECT ba from BankAccount ba ORDER BY ba.accountNumber ASC" )
} )
@XmlRootElement
@Table( schema = "public", name = "bank_account", uniqueConstraints = @UniqueConstraint( columnNames = "id" ) )
public class BankAccount extends IdentifiableEntity {

  public static final String FIND_ALL = "BankAccount.findAll";
  public static final String FIND_BY_NAME = "BankAccount.findByName";
  public static final String FIND_BY_NUMBER = "BankAccount.findByNumber";
  public static final String FIND_BY_USER = "BankAccount.findByUser";

  @NotEmpty
  @Column( name = "account_number", nullable = false, unique = true )
  @Size( min = 1, max = 22 )
  @Pattern( regexp = "[A-Za-z0-9]*", message = "The account number must contain only letters and digits" )
  private String accountNumber;

  @OneToMany
  @JoinColumn( name = "user_id" )
  private List<User> users = new ArrayList<>();

  @NotEmpty
  @Column( name = "account_name", nullable = false, unique = true )
  @Size( min = 1, max = 255 )
  private String accountName;

  @OneToMany( mappedBy = "bankAccount" )
  private List<Balance> balances = new ArrayList<>();

  @NotNull
  @Column( name = "blocked", nullable = false )
  @ColumnDefault( "false" )
  private Boolean blocked;

  @NotEmpty
  @Column( name = "account_currency", nullable = false )
  private String accountCurrency;

  public BankAccount()
  {
    // empty constructor for hibernate
  }

  public BankAccount( BankAccountDTO bankAccountDTO )
  {
    this.accountNumber = bankAccountDTO.getAccountNumber();
    this.accountName = bankAccountDTO.getAccountName();
    this.blocked = bankAccountDTO.getBlocked();
    this.accountCurrency = bankAccountDTO.getAccountCurrency();
  }

  public String getAccountNumber()
  {
    return accountNumber;
  }

  public void setAccountNumber( String accountNumber )
  {
    this.accountNumber = accountNumber;
  }

  public List<User> getUsers()
  {
    return users;
  }

  public void setUsers( List<User> users )
  {
    this.users = users;
  }

  public void addUser( User user )
  {
    users.add( user );
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

  public List<Balance> getBalances()
  {
    return balances;
  }

  public void setBalances( List<Balance> balances )
  {
    this.balances = balances;
  }

  public String getAccountCurrency()
  {
    return accountCurrency;
  }

  public void setAccountCurrency( String accountCurrency )
  {
    this.accountCurrency = accountCurrency;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder( super.toString() );
    sb.append( ", accountNumber=" );
    sb.append( accountNumber );
    sb.append( ", accountName=" );
    sb.append( accountName );
    sb.append( ", accountCurrency=" );
    sb.append( accountCurrency );
    sb.append( ", blocked=" );
    sb.append( blocked );
    sb.append( " ]" );

    return sb.toString();
  }
}
