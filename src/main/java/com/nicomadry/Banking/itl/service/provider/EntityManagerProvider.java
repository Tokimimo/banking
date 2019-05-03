package com.nicomadry.Banking.itl.service.provider;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * This provider helps with the injection of a {@link EntityManager} in the correct {@link PersistenceContext}.
 */
@Dependent
public class EntityManagerProvider {

  @PersistenceContext
  private EntityManager entityManager;

  @Produces
  public EntityManager provideEntityManager()
  {
    return entityManager;
  }

}
