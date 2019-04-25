package com.nicomadry.Banking.api.repo;

import com.nicomadry.Banking.api.data.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

@Dependent
public class UserRepository {

  private EntityManager entityManager;

  private Logger logger;

  @Inject
  public void init(Logger logger,EntityManager entityManager)
  {
    this.logger = logger;
    this.entityManager = entityManager;
  }

  public User findById(Long id)
  {
    return entityManager.find(User.class, id);
  }

  @SuppressWarnings("unchecked")
  public List<User> findAllOrderedByName()
  {
    Session session = (Session) entityManager.getDelegate();
    Criteria cb = session.createCriteria(User.class);

    cb.addOrder(Order.asc("username"));

    return (List<User>) cb.list();
  }

}
