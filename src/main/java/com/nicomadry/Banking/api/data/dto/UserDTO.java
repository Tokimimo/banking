package com.nicomadry.Banking.api.data.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {

  private String username;

  private String password;

  private String address;

  public UserDTO(String username, String password, String address)
  {
    this.username = username;
    this.password = password;
    this.address = address;
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress(String address)
  {
    this.address = address;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }
}
