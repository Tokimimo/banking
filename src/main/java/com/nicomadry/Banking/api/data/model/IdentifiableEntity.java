package com.nicomadry.Banking.api.data.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;

@MappedSuperclass
public abstract class IdentifiableEntity implements Entity {
  /**
   * the serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  @Id
  @NotEmpty
  @Column(name = "ID", precision = 9, scale = 0, nullable = false, updatable = false)
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
