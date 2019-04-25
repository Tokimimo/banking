package com.nicomadry.Banking.api.data.model;

import java.io.Serializable;

public interface Identifiable extends Serializable {

  /**
   * Gets the unique id of the entity.
   *
   * @return the id
   */
  Long getId();

}
