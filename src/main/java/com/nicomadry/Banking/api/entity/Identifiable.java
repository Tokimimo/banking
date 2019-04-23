package com.nicomadry.Banking.api.entity;

import java.io.Serializable;

public interface Identifiable extends Serializable {

  /**
   * Gets the unique id of the entity.
   *
   * @return the id
   */
  Long getId();

}
