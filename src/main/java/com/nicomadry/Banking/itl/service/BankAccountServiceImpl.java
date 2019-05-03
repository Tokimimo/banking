package com.nicomadry.Banking.itl.service;

import com.nicomadry.Banking.api.data.dto.BankAccountDTO;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.api.repo.BankAccountRepository;
import com.nicomadry.Banking.api.repo.UserRepository;
import com.nicomadry.Banking.api.service.BankAccountService;
import com.nicomadry.Banking.api.service.CurrencyService;
import com.nicomadry.Banking.api.service.IBANService;
import com.nicomadry.Banking.itl.exception.*;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.nicomadry.Banking.itl.util.QueryUtils.getSingleResult;
import static java.util.Objects.requireNonNull;

@Dependent
@Typed( BankAccountService.class )
public class BankAccountServiceImpl implements BankAccountService {

  private Logger log;
  private UserRepository userRepository;
  private BankAccountRepository bankAccountRepository;
  private EntityManager entityManager;
  private CurrencyService currencyService;
  private IBANService ibanService;

  @Inject
  public void init( Logger log, UserRepository userRepository, BankAccountRepository bankAccountRepository, EntityManager entityManager,
                    CurrencyService currencyService, IBANService ibanService )
  {
    this.log = log;
    this.userRepository = userRepository;
    this.bankAccountRepository = bankAccountRepository;
    this.entityManager = entityManager;
    this.currencyService = currencyService;
    this.ibanService = ibanService;
  }

  @Override
  public BankAccount createBankAccount( Long userId, BankAccountDTO bankAccountDTO ) throws NotFoundException, NotCreatableException
  {
    requireNonNull( userId, "The user id must not be null" );
    requireNonNull( bankAccountDTO, "The bankAccountDTO must not be null" );

    final User user = getUserById( userId );

    return createBankAccount( user, bankAccountDTO );
  }

  @Override
  public BankAccount createBankAccount( User user, BankAccountDTO bankAccountDTO ) throws NotCreatableException
  {
    requireNonNull( user, "The user must not be null" );
    requireNonNull( bankAccountDTO, "The bankAccountDTO must not be null" );

    checkIbanValidity( bankAccountDTO );
    checkCurrencyValidity( bankAccountDTO );

    final BankAccount newAccount = new BankAccount( bankAccountDTO );
    newAccount.addUser( user );

    final BankAccount createdAccount = bankAccountRepository.createBankAccount( newAccount );

    log.debugf( "Created bank account {0}", createdAccount.getAccountName() );

    return createdAccount;
  }

  private void checkIbanValidity( BankAccountDTO bankAccountDTO ) throws NotCreatableException
  {
    try {
      final boolean valid = ibanService.isIbanValid( bankAccountDTO.getAccountNumber() );

      if ( !valid ) {
        throw new NotCreatableException( "The IBAN: " + bankAccountDTO.getAccountNumber() + " is not valid" );
      }
    }
    catch ( HTTPRequestException | DeserializationException e ) {
      throw new NotCreatableException(
              "The bank account: " + bankAccountDTO.getAccountName() + " couldn't be created, because the IBAN couldn't be validated", e );
    }
  }

  /**
   * Checks if the currency set for the account is valid
   *
   * @param bankAccountDTO The {@link BankAccountDTO bank account} to check for
   *
   * @throws NotCreatableException If the validity couldn't be verified or it is not valid
   */
  private void checkCurrencyValidity( BankAccountDTO bankAccountDTO ) throws NotCreatableException
  {
    try {
      final boolean isValid = currencyService.isValidCurrency( bankAccountDTO.getAccountCurrency() );

      if ( !isValid ) {
        throw new NotCreatableException( "The bank account: " + bankAccountDTO.getAccountName() + " couldn't be created" );
      }
    }
    catch ( HTTPRequestException | DeserializationException e ) {
      log.error( "Couldn't verify currency", e );
      throw new NotCreatableException( "The bank account: " + bankAccountDTO.getAccountName() + " couldn't be created",
                                       e );
    }
  }

  @Override
  public BankAccount addUserToAccount( Long userId, BankAccount bankAccount ) throws NotFoundException, NotUpdatableException
  {
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( bankAccount, "The bankAccount must not be null" );

    final User user = getUserById( userId );

    return addUserToAccount( user, bankAccount );
  }

  @Override
  public BankAccount addUserToAccount( String username,
                                       BankAccount bankAccount ) throws NotFoundException, NotUpdatableException
  {
    requireNonNull( username, "The username must not be null" );
    requireNonNull( bankAccount, "The bankAccount must not be null" );

    final User user = getUserByName( username );

    return addUserToAccount( user, bankAccount );
  }

  @Override
  public BankAccount addUserToAccount( User user, BankAccount bankAccount ) throws NotUpdatableException
  {
    requireNonNull( user, "The user must not be null" );
    requireNonNull( bankAccount, "The bankAccount must not be null" );

    List<User> accountUsers = bankAccount.getUsers();
    log.debugf( "Old user list: {0}", accountUsers );
    accountUsers.add( user );

    final BankAccount updatedAccount = bankAccountRepository.updateBankAccount( bankAccount );

    log.debugf( "New user list: {0}", updatedAccount.getUsers() );

    return updatedAccount;
  }

  @Override
  public BankAccount removeUserFromAccount( Long userId, BankAccount bankAccount ) throws NotFoundException, NotUpdatableException
  {
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( bankAccount, "The bankAccount must not be null" );

    final User user = getUserById( userId );

    return removeUserFromAccount( user, bankAccount );
  }

  @Override
  public BankAccount removeUserFromAccount( String username, BankAccount bankAccount ) throws NotFoundException, NotUpdatableException
  {
    requireNonNull( username, "The username must not be null" );
    requireNonNull( bankAccount, "The bankAccount must not be null" );

    final User user = getUserByName( username );

    return removeUserFromAccount( user, bankAccount );
  }

  @Override
  public BankAccount removeUserFromAccount( User user, BankAccount bankAccount ) throws NotUpdatableException
  {
    requireNonNull( user, "The user must not be null" );
    requireNonNull( bankAccount, "The bankAccount must not be null" );

    List<User> accountUsers = bankAccount.getUsers();

    log.debugf( "Old user list: {0}", accountUsers );

    final List<User> filteredUser = accountUsers.stream().filter( accUser -> !accUser.getUsername().equals( user.getUsername() ) )
                                                .collect( Collectors.toList() );

    log.debugf( "New user list: {0}", filteredUser );

    bankAccount.setUsers( filteredUser );
    return bankAccountRepository.updateBankAccount( bankAccount );
  }

  @Override
  public List<User> listUserOfAccount( Long bankAccountId ) throws NotFoundException
  {
    requireNonNull( bankAccountId, "The bankAccountId must not be null" );

    final Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( bankAccountId );

    final BankAccount bankAccount = bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account found for ID:" + bankAccountId ) );

    return listUserOfAccount( bankAccount );
  }

  @Override
  public List<User> listUserOfAccount( BankAccount bankAccount )
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );

    return bankAccount.getUsers();
  }

  @Override
  public List<BankAccount> listAccountsOfUser( Long userId ) throws NotFoundException
  {
    requireNonNull( userId, "The userId must not be null" );

    final User user = getUserById( userId );

    return listAccountsOfUser( user );
  }

  @Override
  public List<BankAccount> listAccountsOfUser( User user )
  {
    requireNonNull( user, "The user must not be null" );

    final TypedQuery<BankAccount> query = entityManager.createNamedQuery( BankAccount.FIND_BY_USER, BankAccount.class );
    query.setParameter( "userId", user.getId() );

    return query.getResultList();
  }

  @Override
  public Optional<BankAccount> getBankAccount( BankAccountDTO bankAccountDTO )
  {
    requireNonNull( bankAccountDTO, "The  bankAccountDTO must not be null" );

    final Long bankAccountId = bankAccountDTO.getId();

    if ( bankAccountId != null ) {
      return bankAccountRepository.findById( bankAccountId );
    }

    final String bankAccountName = bankAccountDTO.getAccountName();

    if ( bankAccountName != null ) {
      final Optional<BankAccount> bankAccountOptional = getBankAccountByName( bankAccountName );

      if ( bankAccountOptional.isPresent() ) {
        return bankAccountOptional;
      }
    }

    final String bankAccountNumber = bankAccountDTO.getAccountNumber();

    if ( bankAccountNumber != null ) {
      return getBankAccountByIban( bankAccountNumber );
    }

    return Optional.empty();
  }

  @Override
  public Optional<BankAccount> getBankAccountByIban( String accountIban )
  {
    requireNonNull( accountIban, "The accountIban must not be null" );

    final TypedQuery<BankAccount> bankAccountByNumberQuery = entityManager
            .createNamedQuery( BankAccount.FIND_BY_NUMBER, BankAccount.class );
    bankAccountByNumberQuery.setParameter( "accountNumber", accountIban );

    return getSingleResult( bankAccountByNumberQuery );
  }

  @Override
  public Optional<BankAccount> getBankAccountByName( String accountName )
  {
    requireNonNull( accountName, "The accountName must not be null" );

    final TypedQuery<BankAccount> bankAccountByNameQuery = entityManager.createNamedQuery( BankAccount.FIND_BY_NAME, BankAccount.class );
    bankAccountByNameQuery.setParameter( "accountName", accountName );

    return getSingleResult( bankAccountByNameQuery );
  }

  private User getUserById( Long userId ) throws NotFoundException
  {
    final Optional<User> userOptional = userRepository.findById( userId );
    final User user = userOptional.orElseThrow( () -> new NotFoundException( "No user found for ID: " + userId ) );

    log.debugf( "Found user {0}", user.getUsername() );
    return user;
  }

  private User getUserByName( String username ) throws NotFoundException
  {
    final Optional<User> userOptional = userRepository.findByName( username );
    final User user = userOptional.orElseThrow( () -> new NotFoundException( "No user found for name: " + username ) );

    log.debugf( "Found user {0}", user.getUsername() );
    return user;
  }

}
