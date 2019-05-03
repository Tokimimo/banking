package com.nicomadry.Banking.api.data.enums;

/**
 * This enum is used to select the order by clause in queries, where the ordering is controlled by the user
 */
public enum OrderingType {

  ASC( "ASC" ),
  DESC( "DESC" );

  private String name;

  OrderingType( String name )
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }
}
