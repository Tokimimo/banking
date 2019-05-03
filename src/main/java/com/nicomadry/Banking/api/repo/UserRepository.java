package com.nicomadry.Banking.api.repo;

import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.itl.exception.*;
import com.nicomadry.Banking.itl.util.PasswordUtils;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static com.nicomadry.Banking.itl.util.PasswordUtils.getSalt;

@Dependent
public class UserRepository {

  private EntityManager entityManager;
  private Logger logger;

  @Inject
  public void init( Logger logger, EntityManager entityManager )
  {
    this.logger = logger;
    this.entityManager = entityManager;
  }

  /**
   * Tries to find the {@link User} for the given ID.
   *
   * @param id The ID of the {@link User}
   *
   * @return An {@link Optional} possibly containing the {@link User}
   */
  public Optional<User> findById( Long id )
  {
    return Optional.ofNullable( entityManager.find( User.class, id ) );
  }

  /**
   * Tries to find the {@link User} for the given name.
   *
   * @param username The name of the {@link User}
   *
   * @return An {@link Optional} possibly containing the {@link User}
   */
  public Optional<User> findByName( String username )
  {
    TypedQuery<User> query = entityManager.createNamedQuery( User.FIND_BY_NAME, User.class );
    query.setParameter( "username", username );

    try {
      return Optional.ofNullable( query.getSingleResult() );
    }
    catch ( Exception e ) {
      return Optional.empty();
    }

  }

  /**
   * Returns all {@link User User's} currently in the system.
   *
   * @return The list of all {@link User User's}
   */
  public List<User> findAllOrderedByName()
  {
    TypedQuery<User> query = entityManager.createNamedQuery( User.FIND_ALL, User.class );
    return query.getResultList();
  }

  /**
   * Persists the given {@link UserDTO} as {@link User} in the database.
   *
   * @param userDTO The {@link UserDTO} holding all information's to be persisted
   *
   * @return The persisted {@link User}
   *
   * @throws NotUniqueException    If the username of the {@link User} is not unique
   * @throws NotCreatableException If the {@link User} couldn't be created
   */
  public User createUser( UserDTO userDTO ) throws NotUniqueException, NotCreatableException
  {
    if ( !isUsernameUnique( userDTO.getUsername() ) ) {
      throw new NotUniqueException( "The username: " + userDTO.getUsername() + " is not unique" );
    }

    String salt = getSalt();
    logger.debug( "Generated Salt: " + salt );

    User user = new User( userDTO );

    user.setPasswordSalt( salt );
    user.setPassword( PasswordUtils.hashPassword( userDTO.getPassword(), salt ) );

    try {
      entityManager.persist( user );
    }
    catch ( Exception e ) {
      throw new NotCreatableException( "The user: " + user.getUsername() + " couldn't be created", e );
    }

    return user;
  }

  /**
   * This function will check if the username is already existent in the system.
   *
   * @param username The username to check for
   *
   * @return True, if the username is existent, false otherwise
   */
  private boolean isUsernameUnique( String username )
  {
    TypedQuery<User> query = entityManager.createNamedQuery( User.FIND_BY_NAME, User.class );
    query.setParameter( "username", username );

    final List<User> userList = query.getResultList();

    return userList.isEmpty();
  }

  /**
   * Updates the given {@link User} with the new information's which are in the {@link UserDTO}.
   *
   * @param userId  The ID of the {@link User} which will be updated
   * @param userDTO The {@link UserDTO} holding the new information's
   *
   * @return The updated {@link User}
   *
   * @throws NotUpdatableException If the {@link User} couldn't be updated
   * @throws NotFoundException     If no {@link User} was found for the given ID
   */
  public User updateUser( Long userId, UserDTO userDTO ) throws NotUpdatableException, NotFoundException
  {
    final Optional<User> userOptional = findById( userId );

    User user = userOptional.orElseThrow( () -> new NotFoundException( "No User for the requested ID found" ) );

    return updateUser( user, userDTO );
  }

  /**
   * Updates the given {@link User} with the new information's which are in the {@link UserDTO}.
   *
   * @param user    The {@link User} which will be updated
   * @param userDTO The {@link UserDTO} holding the new information's
   *
   * @return The updated {@link User}
   *
   * @throws NotUpdatableException If the {@link User} couldn't be updated
   */
  public User updateUser( User user, UserDTO userDTO ) throws NotUpdatableException
  {
    if ( userDTO.getUsername() != null ) {
      throw new NotUpdatableException( "The username cannot be changed" );
    }

    if ( userDTO.getPassword() != null ) {
      final String salt = getSalt();
      final String hashedPassword = PasswordUtils.hashPassword( userDTO.getPassword(), salt );

      user.setPasswordSalt( salt );
      user.setPassword( hashedPassword );
    }

    if ( userDTO.getAddress() != null ) {
      user.setAddress( userDTO.getAddress() );
    }

    try {
      // TODO: Research if lock is needed
      entityManager.merge( user );
    }
    catch ( Exception e ) {
      throw new NotUpdatableException( "The user: " + user.getUsername() + "couldn't be updated", e );
    }

    return user;
  }

  /**
   * Deletes the given {@link User}.
   *
   * @param userId The ID of the {@link User} which will be deleted
   *
   * @throws NotFoundException     If no {@link User} was found for the given ID
   * @throws NotDeletableException If the {@link User} cannot be deleted
   */
  public void deleteUser( Long userId ) throws NotFoundException, NotDeletableException
  {
    final Optional<User> userOptional = findById( userId );
    User user = userOptional.orElseThrow( () -> new NotFoundException( "No User for the requested ID found" ) );

    deleteUser( user );
  }

  /**
   * Deletes the given {@link User}.
   *
   * @param user The {@link User} which will be deleted
   *
   * @throws NotDeletableException If the {@link User} cannot be deleted
   */
  public void deleteUser( User user ) throws NotDeletableException
  {
    try {
      entityManager.remove( user );
    }
    catch ( Exception e ) {
      throw new NotDeletableException( "The user: " + user.getUsername() + " couldn't be deleted", e );
    }
  }

}
