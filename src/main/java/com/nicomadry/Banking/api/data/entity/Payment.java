package com.nicomadry.Banking.api.data.entity;

import com.nicomadry.Banking.api.data.enums.PaymentStatus;
import com.nicomadry.Banking.api.data.model.IdentifiableEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.ZonedDateTime;

@Entity
@XmlRootElement
@Table(schema = "public", name = "payment", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Payment extends IdentifiableEntity {

  @NotNull
  @Column(name = "amount", nullable = false)
  private float amount;

  @NotEmpty
  @Column(name = "currency", nullable = false)
  @Size(min = 1, max = 20)
  private String currency;

  @OneToOne
  @JoinColumn(name = "giver_account_id")
  private BankAccount giverAccount;

  @NotEmpty
  @Column(name = "beneficiary_account_number", nullable = false)
  @Size(min = 1, max = 20)
  private String beneficiaryAccountNumber;

  @NotEmpty
  @Column(name = "beneficiary_Name", nullable = false)
  @Size(min = 1, max = 20)
  private String beneficiaryName;

  @Column(name = "communication")
  @Size(min = 1, max = 20)
  private String communication;

  @NotNull
  @Column(name = "creation_date", nullable = false)
  private ZonedDateTime creationDate;

  @NotEmpty
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Size(max = 8)
  private PaymentStatus status;

  public Payment()
  {
    // empty constructor for hibernate
  }

  public float getAmount()
  {
    return amount;
  }

  public void setAmount(float amount)
  {
    this.amount = amount;
  }

  public String getCurrency()
  {
    return currency;
  }

  public void setCurrency(String currency)
  {
    this.currency = currency;
  }

  public BankAccount getGiverAccount()
  {
    return giverAccount;
  }

  public void setGiverAccount(BankAccount giverAccount)
  {
    this.giverAccount = giverAccount;
  }

  public String getBeneficiaryAccountNumber()
  {
    return beneficiaryAccountNumber;
  }

  public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber)
  {
    this.beneficiaryAccountNumber = beneficiaryAccountNumber;
  }

  public String getBeneficiaryName()
  {
    return beneficiaryName;
  }

  public void setBeneficiaryName(String beneficiaryName)
  {
    this.beneficiaryName = beneficiaryName;
  }

  public String getCommunication()
  {
    return communication;
  }

  public void setCommunication(String communication)
  {
    this.communication = communication;
  }

  public ZonedDateTime getCreationDate()
  {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate)
  {
    this.creationDate = creationDate;
  }

  public PaymentStatus getStatus()
  {
    return status;
  }

  public void setStatus(PaymentStatus status)
  {
    this.status = status;
  }
}
