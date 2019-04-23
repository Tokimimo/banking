package com.nicomadry.Banking.itl.service.provider;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Dependent
public class EntityManagerProvider {

  @PersistenceContext( unitName = "primary")
  private EntityManager entityManager;

  @Produces
  public EntityManager provideEntityManager()
  {
    return entityManager;
  }

}
