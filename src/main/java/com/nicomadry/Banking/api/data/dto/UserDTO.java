package com.nicomadry.Banking.api.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nicomadry.Banking.api.data.entity.User;

public class UserDTO extends IdentifiableDTO {

  private String username;

  private String password;

  private String address;

  @JsonCreator
  public UserDTO( @JsonProperty( "userId" ) Long userId, @JsonProperty( "username" ) String username,
                  @JsonProperty( "password" ) String password,
                  @JsonProperty( "address" ) String address )
  {
    this.id = userId;
    this.username = username;
    this.password = password;
    this.address = address;
  }

  public UserDTO( User user )
  {
    super( user );
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.address = user.getAddress();
  }

  public String getAddress()
  {
    return address;
  }

  public void setAddress( String address )
  {
    this.address = address;
  }

  @JsonIgnore
  public String getPassword()
  {
    return password;
  }

  public void setPassword( String password )
  {
    this.password = password;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername( String username )
  {
    this.username = username;
  }

  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer( super.toString() );
    sb.append( ", username=" );
    sb.append( username );
    sb.append( ", address=" );
    sb.append( address );
    sb.append( "];" );

    return sb.toString();
  }
}
