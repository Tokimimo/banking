package com.nicomadry.Banking.api.repo;

import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.itl.util.PasswordUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Dependent
public class UserRepository {

  private EntityManager entityManager;

  private Logger logger;

  @Inject
  public void init(Logger logger, EntityManager entityManager)
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
    TypedQuery<User> query = entityManager.createNamedQuery(User.FIND_ALL, User.class);
    return query.getResultList();
  }

  public void createUser(UserDTO userDTO)
  {
    if (!isUsernameUnique(userDTO.getUsername())) {
      // TODO: Throw Exception
    }

    String salt = PasswordUtils.getSalt();
    logger.info("Generated Salt: " + salt);

    User user = new User(userDTO);

    user.setPasswordSalt(salt);
    user.setPassword(PasswordUtils.hashPassword(userDTO.getPassword(), salt));

    entityManager.persist(user);
  }

  private boolean isUsernameUnique(String username)
  {
    TypedQuery<User> query = entityManager.createNamedQuery(User.FIND_BY_NAME, User.class);
    query.setParameter("username", username);

    final List<User> userList = query.getResultList();

    return userList.isEmpty();
  }

}
