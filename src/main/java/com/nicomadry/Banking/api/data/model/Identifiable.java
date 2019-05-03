package com.nicomadry.Banking.api.data.model;

import java.io.Serializable;

/**
 * This Interface is used to signal that a class is identifiable over an ID.
 */
public interface Identifiable extends Serializable {

  /**
   * Gets the unique id of the entity.
   *
   * @return the id
   */
  Long getId();

}
