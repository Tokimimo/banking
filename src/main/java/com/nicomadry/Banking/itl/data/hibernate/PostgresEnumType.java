package com.nicomadry.Banking.itl.data.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.EnumType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * This class is used to register {@link Enum Enum's} for usage in the postgres database.
 */
public class PostgresEnumType extends EnumType {

  public void nullSafeSet(
          PreparedStatement st,
          Object value,
          int index,
          SharedSessionContractImplementor session )
          throws HibernateException, SQLException
  {
    if ( value == null ) {
      st.setNull( index, Types.OTHER );
    }
    else {
      st.setObject(
              index,
              value.toString(),
              Types.OTHER
      );
    }
  }
}
