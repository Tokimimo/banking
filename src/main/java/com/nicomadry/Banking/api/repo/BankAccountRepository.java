package com.nicomadry.Banking.api.repo;

import com.nicomadry.Banking.api.data.dto.BankAccountDTO;
import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.itl.exception.*;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Dependent
public class BankAccountRepository {

  private Logger logger;
  private EntityManager entityManager;

  @Inject
  public void init( Logger logger, EntityManager entityManager )
  {
    this.logger = logger;
    this.entityManager = entityManager;
  }

  /**
   * Tries to find the {@link BankAccount} for the given ID.
   *
   * @param id The ID of the {@link BankAccount}
   *
   * @return An {@link Optional} possibly containing the {@link BankAccount}
   */
  public Optional<BankAccount> findById( Long id )
  {
    requireNonNull( id, "The ID must not be null" );

    return Optional.ofNullable( entityManager.find( BankAccount.class, id ) );
  }

  /**
   * Returns all {@link BankAccount BankAccounts} in the system ordered by their name.
   *
   * @return A list of all {@link BankAccount BankAccounts}
   */
  public List<BankAccount> findAllOrderedByName()
  {
    TypedQuery<BankAccount> query = entityManager.createNamedQuery( BankAccount.FIND_ALL, BankAccount.class );
    return query.getResultList();
  }

  /**
   * Persists the {@link BankAccountDTO} as a {@link BankAccount} in the database.
   *
   * @param bankAccountDTO The {@link BankAccountDTO} holding all informations to be persisted
   *
   * @return The persisted {@link BankAccount}
   *
   * @throws NotCreatableException If the {@link BankAccount} couldn't be persisted
   * @throws ValidationException   If the {@link User Users} associated couldn't be found ( only if present )
   */
  public BankAccount createBankAccount(
          BankAccountDTO bankAccountDTO ) throws NotCreatableException, ValidationException
  {
    requireNonNull( bankAccountDTO, "The bank account dto must not be null" );

    BankAccount bankAccount = new BankAccount( bankAccountDTO );

    if ( bankAccountDTO.getUsers() != null ) {
      final List<UserDTO> userDTOS = bankAccountDTO.getUsers();
      final List<User> users = retrieveUserForDTOs( userDTOS );
      bankAccount.setUsers( users );
    }

    return createBankAccount( bankAccount );
  }

  /**
   * Persists the {@link BankAccount} in the database.
   *
   * @param bankAccount The {@link BankAccount} to be persisted
   *
   * @return The persisted {@link BankAccount}
   *
   * @throws NotCreatableException If the {@link BankAccount} couldn't be persisted
   */
  public BankAccount createBankAccount( BankAccount bankAccount ) throws NotCreatableException
  {
    try {
      entityManager.persist( bankAccount );
    }
    catch ( Exception e ) {
      throw new NotCreatableException( "The bank account: " + bankAccount.getAccountName() + " couldn't be created",
                                       e );
    }

    return bankAccount;
  }

  /**
   * Retrieves the list of {@link User Users} associated to the {@link BankAccount}.
   *
   * @param userDTOS The list of associated {@link UserDTO UserDTOs}
   *
   * @return The list of found {@link User}
   *
   * @throws ValidationException If not all {@link User} could be found in the system
   */
  private List<User> retrieveUserForDTOs( List<UserDTO> userDTOS ) throws ValidationException
  {
    final List<String> usernames = userDTOS.stream().map( UserDTO::getUsername ).collect( Collectors.toList() );

    final TypedQuery<User> userTypedQuery = entityManager
            .createQuery( "select u from User u where u.username in (:usernames)", User.class );
    userTypedQuery.setParameter( "usernames", usernames );

    final List<User> foundUser = userTypedQuery.getResultList();

    final List<String> missingUsers = foundUser.stream().map( User::getUsername ).filter( username -> !usernames.contains( username ) )
                                               .collect( Collectors.toList() );

    if ( !missingUsers.isEmpty() ) {
      throw new ValidationException( "The users: " + missingUsers + " were not found in the system" );
    }

    return foundUser;
  }

  /**
   * Updates the {@link BankAccount} with the information's of the given {@link BankAccountDTO}.
   *
   * @param bankAccountId  The ID of the {@link BankAccount} to be updated
   * @param bankAccountDTO The {@link BankAccountDTO} holding the information's
   *
   * @return The updated {@link BankAccount}
   *
   * @throws NotUpdatableException If the {@link BankAccount} couldn't be updated
   * @throws NotFoundException     If no {@link BankAccount} could be found for the given ID
   * @throws ValidationException   If the {@link User Users} to be added couldn't be found
   */
  public BankAccount updateBankAccount( Long bankAccountId,
                                        BankAccountDTO bankAccountDTO ) throws NotUpdatableException, NotFoundException, ValidationException
  {
    requireNonNull( bankAccountId, "The bankAccountId must not be null" );
    requireNonNull( bankAccountDTO, "The bankAccountDTO must not be null" );

    final Optional<BankAccount> bankAccountOptional = findById( bankAccountId );

    final BankAccount bankAccount = bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account for ID: " + bankAccountId + " found." ) );

    return updateBankAccount( bankAccount, bankAccountDTO );
  }

  /**
   * Updates the {@link BankAccount} with the new information's.
   *
   * @param bankAccount The {@link BankAccount} with unpersisted updates
   *
   * @return The updated {@link BankAccount}
   *
   * @throws NotUpdatableException If the {@link BankAccount} couldn't be updated
   */
  public BankAccount updateBankAccount( BankAccount bankAccount ) throws NotUpdatableException
  {
    try {
      // TODO: Research if lock is needed
      entityManager.merge( bankAccount );
    }
    catch ( Exception e ) {
      throw new NotUpdatableException( "The bank account: " + bankAccount.getAccountName() + "couldn't be updated", e );
    }

    return bankAccount;
  }

  /**
   * Updates the {@link BankAccount} with the information's of the given {@link BankAccountDTO}.
   *
   * @param bankAccount    The {@link BankAccount} to be updated
   * @param bankAccountDTO The {@link BankAccountDTO} holding the new information's
   *
   * @return The updated {@link BankAccount}
   *
   * @throws NotUpdatableException If the {@link BankAccount} couldn't be updated
   * @throws ValidationException   If the {@link User Users} associated couldn't be found ( only if present )
   */
  public BankAccount updateBankAccount( BankAccount bankAccount,
                                        BankAccountDTO bankAccountDTO ) throws NotUpdatableException, ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( bankAccountDTO, "The bankAccountDTO must not be null" );

    if ( bankAccountDTO.getAccountNumber() != null ) {
      throw new NotUpdatableException( "The account number cannot be changed" );
    }

    if ( bankAccountDTO.getAccountName() != null ) {
      throw new NotUpdatableException( "The account name cannot be changed" );
    }

    if ( bankAccountDTO.getUsers() != null ) {
      final List<User> newUsers = retrieveUserForDTOs( bankAccountDTO.getUsers() );
      final List<User> currentUsers = bankAccount.getUsers();

      currentUsers.addAll( newUsers );
      bankAccount.setUsers( currentUsers );
    }

    if ( bankAccountDTO.getAccountCurrency() != null ) {
      bankAccount.setAccountCurrency( bankAccountDTO.getAccountCurrency() );
    }

    return updateBankAccount( bankAccount );
  }

  /**
   * Deletes the {@link BankAccount} in the database.
   *
   * @param bankAccountId The ID of the {@link BankAccount} to be deleted
   *
   * @throws NotFoundException     If no {@link BankAccount} could be found for the given ID
   * @throws NotDeletableException If the {@link BankAccount} couldn't be deleted
   */
  public void deleteBankAccount( Long bankAccountId ) throws NotFoundException, NotDeletableException
  {
    requireNonNull( bankAccountId, "The bank account id must not be null" );

    final Optional<BankAccount> bankAccountOptional = findById( bankAccountId );

    final BankAccount bankAccount = bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account for ID: " + bankAccountId + " found" ) );

    deleteBankAccount( bankAccount );
  }

  /**
   * Deletes the {@link BankAccount} in the database.
   *
   * @param bankAccount The {@link BankAccount} to be deleted
   *
   * @throws NotDeletableException If the {@link BankAccount} couldn't be deleted
   */
  public void deleteBankAccount( BankAccount bankAccount ) throws NotDeletableException
  {
    requireNonNull( bankAccount, "The bank account must not be null" );

    try {
      entityManager.remove( bankAccount );
    }
    catch ( Exception e ) {
      throw new NotDeletableException( "The bank account: " + bankAccount.getAccountName() + "couldn't be deleted", e );
    }
  }

}
