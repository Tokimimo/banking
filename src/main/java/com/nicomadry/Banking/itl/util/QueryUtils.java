package com.nicomadry.Banking.itl.util;

import com.nicomadry.Banking.api.data.model.IdentifiableEntity;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TypedQuery;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * This utils class holds helper methods to perform exception safe singleResult queries.
 */
public class QueryUtils {

  /**
   * Private Constructor
   */
  private QueryUtils()
  {
    super();
  }

  /**
   * Performs a sage Single result query for the given {@link TypedQuery}, which will return an {@link Optional} instead of throwing
   * unwanted Hibernate exceptions.
   *
   * @param typedQuery The {@link TypedQuery} which will be queried for a single result
   * @param <T>        The Entity which will be returned
   *
   * @return An {@link Optional} possibly containing the result of the search
   */
  public static <T extends IdentifiableEntity> Optional<T> getSingleResult( TypedQuery<T> typedQuery )
  {
    requireNonNull( typedQuery, "The typedQuery must not be null" );

    try {
      return Optional.of( typedQuery.getSingleResult() );
    }
    catch ( NoResultException | NonUniqueResultException | QueryTimeoutException e ) {
      return Optional.empty();
    }
  }

}
