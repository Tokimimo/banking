package com.nicomadry.Banking.api.data.entity;

import com.nicomadry.Banking.api.data.dto.PaymentDTO;
import com.nicomadry.Banking.api.data.enums.PaymentStatus;
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
        @NamedQuery( name = Payment.FIND_BY_GIVER_ACCOUNT_NAME, query = "SELECT p from Payment p WHERE p.giverAccount.accountName = :giverAccountName" ),
        @NamedQuery( name = Payment.FIND_BY_GIVER_ACCOUNT_NUMBER, query = "SELECT p from Payment p WHERE p.giverAccount.accountNumber = :giverAccountNumber" ),
        @NamedQuery( name = Payment.FIND_BY_GIVER_ACCOUNT, query = "SELECT p from Payment p WHERE p.giverAccount = :giverAccount" )
} )
@XmlRootElement
@Table( schema = "public", name = "payment", uniqueConstraints = @UniqueConstraint( columnNames = "id" ) )
public class Payment extends IdentifiableEntity {

  public static final String FIND_BY_GIVER_ACCOUNT_NAME = "Payment.findByGiverAccountName";
  public static final String FIND_BY_GIVER_ACCOUNT_NUMBER = "Payment.findByGiverAccountNumber";
  public static final String FIND_BY_GIVER_ACCOUNT = "Payment.findByGiverAccount";

  @NotNull
  @Column( name = "amount", nullable = false )
  private BigDecimal amount;

  @NotEmpty
  @Column( name = "currency", nullable = false )
  @Size( min = 1, max = 20 )
  private String currency;

  @OneToOne
  @JoinColumn( name = "giver_account_id" )
  private BankAccount giverAccount;

  @NotEmpty
  @Column( name = "beneficiary_account_number", nullable = false )
  @Size( min = 1, max = 22 )
  private String beneficiaryAccountNumber;

  @NotEmpty
  @Column( name = "beneficiary_Name", nullable = false )
  private String beneficiaryName;

  @Column( name = "communication" )
  private String communication;

  @NotNull
  @Column( name = "creation_date", nullable = false )
  private ZonedDateTime creationDate;

  @NotNull
  @Enumerated( EnumType.STRING )
  @Column( name = "status", nullable = false )
  private PaymentStatus status;

  public Payment()
  {
    // empty constructor for hibernate
  }

  public Payment( PaymentDTO paymentDTO )
  {
    this.amount = paymentDTO.getAmount();
    this.beneficiaryAccountNumber = paymentDTO.getBeneficiaryAccountNumber();
    this.beneficiaryName = paymentDTO.getBeneficiaryName();
    this.communication = paymentDTO.getCommunication();
    this.creationDate = paymentDTO.getCreationDate();
    this.currency = paymentDTO.getCurrency();
    this.giverAccount = paymentDTO.getGiverAccount() != null ? new BankAccount( paymentDTO.getGiverAccount() ) : null;
    this.status = paymentDTO.getStatus();
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

  public BankAccount getGiverAccount()
  {
    return giverAccount;
  }

  public void setGiverAccount( BankAccount giverAccount )
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

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder( super.toString() );
    sb.append( ", amount=" );
    sb.append( amount );
    sb.append( ", currency=" );
    sb.append( currency );
    sb.append( ", giverAccount=" );
    sb.append( giverAccount );
    sb.append( ", beneficiaryAccountNumber=" );
    sb.append( beneficiaryAccountNumber );
    sb.append( ", beneficiaryName=" );
    sb.append( beneficiaryName );
    sb.append( ", status=" );
    sb.append( status );
    sb.append( ", creationDate=" );
    sb.append( creationDate );
    sb.append( " ]" );

    return sb.toString();
  }
}
