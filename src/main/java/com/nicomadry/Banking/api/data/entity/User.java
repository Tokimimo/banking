package com.nicomadry.Banking.api.data.entity;

import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.model.IdentifiableEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@NamedQueries({
        @NamedQuery(name = User.FIND_ALL, query = "SELECT u FROM User u ORDER BY u.username DESC"),
        @NamedQuery(name = User.FIND_BY_NAME, query = "SELECT u FROM User u WHERE u.username = :username"),
        @NamedQuery(name = User.COUNT_ALL, query = "SELECT COUNT(u) FROM User u"),
        @NamedQuery(name = User.VALIDATE_NAME_AND_PASSWORD, query = "SELECT u FROM User u WHERE u.username = :username and u.password = :password")
})
@XmlRootElement
@Table(schema = "public", name = "user", uniqueConstraints = {@UniqueConstraint(name = "UQ_USER_ID", columnNames = "id"), @UniqueConstraint(name = "UQ_USER_USERNAME", columnNames = "username")})
public class User extends IdentifiableEntity {

  public static final String FIND_ALL = "User.findAll";
  public static final String COUNT_ALL = "User.countAll";
  public static final String FIND_BY_NAME = "User.findByName";
  public static final String VALIDATE_NAME_AND_PASSWORD = "User.validateUsernameAndPassword";

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

  public User()
  {
    // empty constructor for hibernate
  }

  public User(String username, String password, String address)
  {
    this.username = username;
    this.password = password;
    this.address = address;
  }

  public User(UserDTO userDTO)
  {
    this.username = userDTO.getUsername();
    this.password = userDTO.getPassword();
    this.address = userDTO.getAddress();
  }

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

  public String getPasswordSalt()
  {
    return passwordSalt;
  }

  public void setPasswordSalt(String passwordSalt)
  {
    this.passwordSalt = passwordSalt;
  }
}
