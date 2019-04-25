package com.nicomadry.Banking.api.data.entity;

import com.nicomadry.Banking.api.data.enums.BalanceType;
import com.nicomadry.Banking.api.data.model.IdentifiableEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(schema = "public", name = "balance", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Balance extends IdentifiableEntity {

  @NotEmpty
  @Column(name = "amount", nullable = false)
  private float amount;

  // TODO: Don't leave it as String?
  @NotEmpty
  @Column(name = "currency", nullable = false)
  private String currency;

  @NotEmpty
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  @Size(min = 9, max = 10)
  private BalanceType type;

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

  public BalanceType getType()
  {
    return type;
  }

  public void setType(BalanceType type)
  {
    this.type = type;
  }
}
