package com.nicomadry.Banking.api.data.entity;

import com.nicomadry.Banking.api.data.model.IdentifiableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(schema = "public", name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class User extends IdentifiableEntity {

  @NotEmpty
  @Size(min = 1, max = 255)
  @Column(name = "username", nullable = false, updatable = false, unique = true)
  @Pattern(regexp = "[A-Za-z ]*", message = "The username must contain only letters and spaces")
  private String username;

  @NotEmpty
  @Column(name = "password", nullable = false)
  @Size(min = 1, max = 255)
  private String password;

  @NotEmpty
  @Column(name = "password_salt", nullable = false)
  @Size(min = 1, max = 255)
  private String passwordSalt;

  @NotEmpty
  @Column(name = "address", nullable = false)
  @Size(min = 1, max = 255)
  private String address;

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
  }
}
