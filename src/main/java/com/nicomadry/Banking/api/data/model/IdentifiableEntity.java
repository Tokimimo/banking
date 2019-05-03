package com.nicomadry.Banking.api.data.model;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class IdentifiableEntity implements Entity {
  /**
   * the serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  @Id
  @NotNull
  @Column(name = "ID", precision = 9, nullable = false, updatable = false)
  @GeneratedValue(strategy= GenerationType.AUTO)
  private Long id;

  @Override
  public Long getId()
  {
    return id;
  }

  @SuppressWarnings("unused")
  public void setId(Long id)
  {
    this.id = id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer(getClass().getName());
    sb.append("[ ID=");
    sb.append(id);

    return sb.toString();
  }
}
