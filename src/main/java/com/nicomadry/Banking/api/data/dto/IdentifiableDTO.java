package com.nicomadry.Banking.api.data.dto;

import com.nicomadry.Banking.api.data.model.IdentifiableEntity;

import java.io.Serializable;

public class IdentifiableDTO implements Serializable {

  protected Long id;

  public IdentifiableDTO()
  {
    // default constructor to let it work with json created objects
  }

  public IdentifiableDTO( IdentifiableEntity identifiableEntity )
  {
    this.id = identifiableEntity.getId();
  }

  public Long getId()
  {
    return id;
  }

  public void setId( Long id )
  {
    this.id = id;
  }

  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer( getClass().getName() );
    sb.append( "[ id=" );
    sb.append( id );

    return sb.toString();
  }
}
