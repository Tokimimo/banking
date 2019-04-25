package com.nicomadry.Banking.api.data.entity;

import com.nicomadry.Banking.api.data.model.IdentifiableEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@XmlRootElement
@Table(schema = "public", name = "bank_account", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class BankAccount extends IdentifiableEntity {

  @NotEmpty
  @Column(name = "account_number", nullable = false)
  @Size(min = 1, max = 20)
  @Pattern(regexp = "[A-Za-z0-9]*", message = "The account number must contain only letters and digits")
  private String accountNumber;

  @OneToMany
  @JoinColumn(name = "user_id" )
  private Set<User> users = new HashSet<>();

  @NotEmpty
  @Column(name = "account_name", nullable = false)
  @Size(min = 1, max = 255)
  private String accountName;

  @OneToMany
  @JoinColumn(name = "balance_id")
  private List<Balance> balances;

  @NotEmpty
  @Column(name = "blocked", nullable = false)
  @ColumnDefault( "false" )
  private Boolean blocked;

  public String getAccountNumber()
  {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber)
  {
    this.accountNumber = accountNumber;
  }

  public Set<User> getUsers()
  {
    return users;
  }

  public void setUsers(Set<User> users)
  {
    this.users = users;
  }

  public String getAccountName()
  {
    return accountName;
  }

  public void setAccountName(String accountName)
  {
    this.accountName = accountName;
  }

  public Boolean getBlocked()
  {
    return blocked;
  }

  public void setBlocked(Boolean blocked)
  {
    this.blocked = blocked;
  }
}
