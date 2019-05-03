package com.nicomadry.Banking.api.data.entity;

import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.enums.BalanceType;
import com.nicomadry.Banking.api.data.model.IdentifiableEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@NamedQueries( {
        @NamedQuery( name = Balance.FIND_NEWEST, query = "SELECT b from Balance b order by b.creationDate DESC" ),
        @NamedQuery( name = Balance.FIND_NEWEST_OF_ACCOUNT, query = "SELECT b from Balance b where b.bankAccount = :bankAccount order by b.creationDate DESC")
} )
@XmlRootElement
@Table( schema = "public", name = "balance", uniqueConstraints = @UniqueConstraint( columnNames = "id" ) )
public class Balance extends IdentifiableEntity {

  public static final String FIND_NEWEST = "Payment.findNewest";
  public static final String FIND_NEWEST_OF_ACCOUNT = "Payment.findNewestOfAccount";

  @NotNull
  @Column( name = "amount", nullable = false )
  private BigDecimal amount;

  // TODO: This could be exchanged with a more robust Enum or Entity ( LATER_VERSION )
  @NotEmpty
  @Column( name = "currency", nullable = false )
  private String currency;

  @NotNull
  @Enumerated( EnumType.STRING )
  @Column( name = "type", nullable = false )
  private BalanceType type;

  @NotNull
  @Column( name = "creation_date", nullable = false )
  private ZonedDateTime creationDate;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bankaccount_id")
  private BankAccount bankAccount;

  public Balance()
  {
    // empty constructor for hibernate
  }

  public Balance( BalanceDTO balanceDTO )
  {
    this.amount = balanceDTO.getAmount();
    this.currency = balanceDTO.getCurrency();
    this.type = balanceDTO.getType();
    this.creationDate = ZonedDateTime.now();
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

  public ZonedDateTime getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate( ZonedDateTime creationDate )
  {
    this.creationDate = creationDate;
  }

  public BankAccount getBankAccount()
  {
    return bankAccount;
  }

  public void setBankAccount( BankAccount bankAccount )
  {
    this.bankAccount = bankAccount;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder( super.toString() );
    sb.append( ", amount=" );
    sb.append( amount );
    sb.append( ", currency=" );
    sb.append( currency );
    sb.append( ", type=" );
    sb.append( type );
    sb.append( ", bankaccount=" );
    sb.append( bankAccount );
    sb.append( ", creationDate=" );
    sb.append( creationDate );
    sb.append( " ]" );

    return sb.toString();
  }
}
