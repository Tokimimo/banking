package com.nicomadry.Banking.itl.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * This utils class holds helper methods to handle date related data.
 */
public class DateUtils {

  /**
   * Hidden constructor
   */
  private DateUtils()
  {
    super();
  }

  /**
   * This function takes the string representation of a {@link ZonedDateTime} and converts it to a {@link ZonedDateTime}. The given string
   * <b>must</b> be in the ISO8601 format.
   *
   * @param dateString The string representation of the {@link ZonedDateTime}
   *
   * @return An {@link Optional} possibly containing the parsed {@link ZonedDateTime}
   */
  public static Optional<ZonedDateTime> stringToDateTime( String dateString )
  {
    requireNonNull( dateString, "The dateString must not be null" );

    try {
      final ZonedDateTime zonedDateTime = ZonedDateTime.parse( dateString );
      return Optional.of( zonedDateTime );
    }
    catch ( DateTimeParseException e ) {
      return Optional.empty();
    }
  }

}
