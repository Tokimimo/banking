package com.nicomadry.Banking.api.repo;

import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.entity.Balance;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.itl.exception.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Dependent
public class BalanceRepository {

  private EntityManager entityManager;
  private BankAccountRepository bankAccountRepository;

  @Inject
  public void init( EntityManager entityManager, BankAccountRepository bankAccountRepository )
  {
    this.entityManager = entityManager;
    this.bankAccountRepository = bankAccountRepository;
  }

  /**
   * Tries to find the {@link Balance} for the given ID
   *
   * @param id The ID of the {@link Balance}
   *
   * @return An {@link Optional} possibly containing the {@link Balance}
   */
  public Optional<Balance> findById( Long id )
  {
    requireNonNull( id, "The ID must not be null" );

    return Optional.ofNullable( entityManager.find( Balance.class, id ) );
  }

  /**
   * Persists the {@link BalanceDTO} as a {@link Balance} in the database.
   *
   * @param balanceDTO The {@link BalanceDTO} holding all information's to be persisted
   *
   * @return The created {@link Balance}
   *
   * @throws NotCreatableException If the {@link Balance} couldn't be created
   */
  public Balance createBalance( BalanceDTO balanceDTO ) throws NotCreatableException
  {
    requireNonNull( balanceDTO, "The balance dto must not be null" );

    Balance balance = new Balance( balanceDTO );

    if ( balanceDTO.getBankAccountDTO() == null ) {
      throw new NotCreatableException( "The balance couldn't be created, because no bank account was given" );
    }

    final Long bankAccountId = balanceDTO.getBankAccountDTO().getId();
    final Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( bankAccountId );

    if ( !bankAccountOptional.isPresent() ) {
      throw new NotCreatableException( "The balance couldn't be created, because no bank account was found for the ID: " + bankAccountId
      );
    }

    balance.setBankAccount( bankAccountOptional.get() );

    try {
      entityManager.persist( balance );
    }
    catch ( Exception e ) {
      throw new NotCreatableException( "The balance: " + balance.getAmount() + " couldn't be created",
                                       e );
    }

    return balance;
  }

  /**
   * This function always throws a {@link NotUpdatableException}, since a {@link Balance} can't be updated.
   *
   * @param balanceId  The ID of the {@link Balance} to update
   * @param balanceDTO The {@link BalanceDTO} holding the new information's
   *
   * @return The updated {@link Balance}
   *
   * @throws NotUpdatableException This is always thrown because a {@link Balance} is <b>not</b> updateable
   */
  public Balance updateBalance( Long balanceId, BalanceDTO balanceDTO ) throws NotUpdatableException
  {
    requireNonNull( balanceId, "The balance id must not be null" );
    requireNonNull( balanceDTO, "The balance dto must not be null" );

    throw new NotUpdatableException( "Updating a balance is not supported" );
  }

  /**
   * This function always throws a {@link NotUpdatableException}, since a {@link Balance} can't be updated.
   *
   * @param balance    The {@link Balance} to update
   * @param balanceDTO The {@link BalanceDTO} holding the new information's
   *
   * @return The updated {@link Balance}
   *
   * @throws NotUpdatableException This is always thrown because a {@link Balance} is <b>not</b> updateable
   */
  public Balance updateBalance( Balance balance, BalanceDTO balanceDTO ) throws NotUpdatableException
  {
    requireNonNull( balance, "The balance must not be null" );
    requireNonNull( balanceDTO, "The balance dto must not be null" );

    throw new NotUpdatableException( "Updating a balance is not supported" );
  }

  /**
   * Deletes the {@link Balance} in the database.
   *
   * @param balanceId The ID of the {@link Balance} to be deleted
   *
   * @throws NotDeletableException If the {@link Balance} couldn't be deleted
   * @throws NotFoundException     If no {@link Balance} was found for the given ID
   */
  public void deleteBalance( Long balanceId ) throws NotDeletableException, NotFoundException
  {
    requireNonNull( balanceId, "The balance id must not be null" );

    final Optional<Balance> balanceOptional = findById( balanceId );
    final Balance balance = balanceOptional.orElseThrow( () -> new NotFoundException( "No balance found for ID: " + balanceId ) );

    deleteBalance( balance );
  }

  /**
   * Deletes the {@link Balance} in the database.
   *
   * @param balance The {@link Balance} to be deleted
   *
   * @throws NotDeletableException If the {@link Balance} couldn't be deleted
   */
  public void deleteBalance( Balance balance ) throws NotDeletableException
  {
    requireNonNull( balance, "The balance must not be null" );

    try {
      entityManager.remove( balance );
    }
    catch ( Exception e ) {
      throw new NotDeletableException( "The balance: " + balance.getId() + "couldn't be deleted", e );
    }
  }

}
