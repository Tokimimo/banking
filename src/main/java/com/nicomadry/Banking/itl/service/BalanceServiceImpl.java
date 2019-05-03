package com.nicomadry.Banking.itl.service;

import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.dto.BankAccountDTO;
import com.nicomadry.Banking.api.data.entity.Balance;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.api.data.enums.BalanceType;
import com.nicomadry.Banking.api.repo.BalanceRepository;
import com.nicomadry.Banking.api.repo.BankAccountRepository;
import com.nicomadry.Banking.api.repo.UserRepository;
import com.nicomadry.Banking.api.service.BankAccountService;
import com.nicomadry.Banking.api.service.CurrencyService;
import com.nicomadry.Banking.api.service.BalanceService;
import com.nicomadry.Banking.itl.exception.NotCreatableException;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.ValidationException;
import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Dependent
@Typed( BalanceService.class )
public class BalanceServiceImpl implements BalanceService {

  private BalanceRepository balanceRepository;
  private BankAccountRepository bankAccountRepository;
  private UserRepository userRepository;
  private EntityManager entityManager;
  private CurrencyService currencyService;
  private BankAccountService bankAccountService;
  private Logger log;

  @Inject
  public void init( BalanceRepository balanceRepository, BankAccountRepository bankAccountRepository, EntityManager entityManager,
                    CurrencyService currencyService, Logger log, UserRepository userRepository, BankAccountService bankAccountService )
  {
    this.balanceRepository = balanceRepository;
    this.bankAccountRepository = bankAccountRepository;
    this.entityManager = entityManager;
    this.currencyService = currencyService;
    this.log = log;
    this.userRepository = userRepository;
    this.bankAccountService = bankAccountService;
  }

  @Override
  public Optional<Balance> getCurrentBalance( Long bankAccountId, Long userId ) throws NotFoundException, ValidationException
  {
    requireNonNull( bankAccountId, "The bankAccountId must not be null" );
    requireNonNull( userId, "The userId must not be null" );

    final Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( bankAccountId );
    final BankAccount bankAccount = bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account found for ID: " + bankAccountId ) );

    return getCurrentBalance( bankAccount, userId );
  }

  @Override
  public Optional<Balance> getCurrentBalance( BankAccount bankAccount, Long userId ) throws NotFoundException, ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( userId, "The userId must not be null" );

    final User user = getUser( userId );

    return getCurrentBalance( bankAccount, user );
  }

  @Override
  public Optional<Balance> getCurrentBalance( BankAccount bankAccount, User user ) throws ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( user, "The user must not be null" );

    List<User> accountUsers = bankAccountService.listUserOfAccount( bankAccount );

    if ( !accountUsers.contains( user ) ) {
      throw new ValidationException(
              "The user: " + user.getUsername() + " is not allowed to retrieve the balance for account: " + bankAccount.getAccountName() );
    }

    return getNewestBalanceOfAccount( bankAccount );
  }

  @Override
  public boolean hasBalanceEnoughFunds( Long bankAccountId, Long userId, BigDecimal amount ) throws NotFoundException, ValidationException
  {
    requireNonNull( bankAccountId, "The  bankAccountId must not be null" );
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( amount, "The amount must not be null" );

    final Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( bankAccountId );
    final BankAccount bankAccount = bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account found for ID: " + bankAccountId ) );

    return hasBalanceEnoughFunds( bankAccount, userId, amount );
  }

  @Override
  public boolean hasBalanceEnoughFunds( BankAccount bankAccount, Long userId,
                                        BigDecimal amount ) throws NotFoundException, ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( amount, "The amount must not be null" );

    final User user = getUser( userId );

    return hasBalanceEnoughFunds( bankAccount, user, amount );
  }

  @Override
  public boolean hasBalanceEnoughFunds( BankAccount bankAccount, User user, BigDecimal amount ) throws ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( user, "The user must not be null" );
    requireNonNull( amount, "The amount must not be null" );

    List<User> accountUsers = bankAccountService.listUserOfAccount( bankAccount );

    if ( !accountUsers.contains( user ) ) {
      throw new ValidationException(
              "The user: " + user.getUsername() + " is not allowed to retrieve the balance for account: " + bankAccount.getAccountName() );
    }

    final Optional<Balance> currentBalanceOptional = getNewestBalanceOfAccount( bankAccount );

    if ( !currentBalanceOptional.isPresent() ) {
      return false;
    }

    final Balance currentBalance = currentBalanceOptional.get();

    return currentBalance.getAmount().compareTo( amount ) >= 0;
  }

  @Override
  public Balance subtractBalance( BankAccount bankAccount, BalanceDTO subtractBalance ) throws NotCreatableException, ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( subtractBalance, "The subtractBalance must not be null" );

    currencyService.checkCurrencyValidity( subtractBalance.getCurrency() );

    final Optional<Balance> balance = getNewestBalanceOfAccount( bankAccount );

    final BigDecimal withdrawAmount = subtractBalance.getAmount();

    final Balance currentBalance = balance
            .orElseThrow( () -> new ValidationException( "Not enough funds to withdraw: " + withdrawAmount ) );

    final BigDecimal currentAmount = currentBalance.getAmount();
    log.debug( "The current balance is: " + currentAmount );

    BigDecimal exchangedWithdrawAmount = currencyService.getCorrectMoneyAmount( bankAccount, subtractBalance );

    if ( currentAmount.compareTo( exchangedWithdrawAmount ) < 0 ) {
      throw new ValidationException( "Not enough funds to withdraw: " + exchangedWithdrawAmount );
    }

    final BalanceDTO newBalance = createUpdatedBalance( balance.get(), exchangedWithdrawAmount, false );
    newBalance.setType( BalanceType.AVAILABLE );
    log.debug( "The new balance is: " + newBalance.getAmount() );

    return balanceRepository.createBalance( newBalance );
  }

  @NotNull
  private User getUser( Long userId ) throws NotFoundException
  {
    final Optional<User> userOptional = userRepository.findById( userId );
    return userOptional.orElseThrow( () -> new NotFoundException( "No user found for ID: " + userId ) );
  }

  @Override
  public Balance addBalance( BankAccount bankAccount, BalanceDTO addBalance ) throws NotCreatableException, ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( addBalance, "The addBalance must not be null" );

    currencyService.checkCurrencyValidity( addBalance.getCurrency() );

    final Optional<Balance> balance = getNewestBalanceOfAccount( bankAccount );

    BalanceDTO newBalance;

    BigDecimal depositAmount = currencyService.getCorrectMoneyAmount( bankAccount, addBalance );

    // Check if a balance is already existent and create a new balance with the correct amount
    if ( balance.isPresent() ) {
      newBalance = createUpdatedBalance( balance.get(), depositAmount, true );
    }
    else {
      newBalance = addBalance;
      newBalance.setAmount( depositAmount );
      newBalance.setBankAccountDTO( new BankAccountDTO( bankAccount ) );
      log.info( "Added bank account to balance" );
    }

    newBalance.setType( BalanceType.AVAILABLE );

    final Balance createdBalance = balanceRepository.createBalance( newBalance );

    log.info( "Created balance: " + createdBalance );

    return createdBalance;
  }

  /**
   * Retrieves the newest {@link Balance} for the given {@link BankAccount}. The newest balance is determined by {@link
   * Balance#getCreationDate()}.
   *
   * @param bankAccount The {@link BankAccount} to retrieve the newest balance for
   *
   * @return An {@link Optional}, possibly containing the balance
   */
  private Optional<Balance> getNewestBalanceOfAccount( BankAccount bankAccount )
  {
    TypedQuery<Balance> balanceTypedQuery = entityManager.createNamedQuery( Balance.FIND_NEWEST_OF_ACCOUNT, Balance.class );
    balanceTypedQuery.setParameter( "bankAccount", bankAccount );

    final List<Balance> balanceList = balanceTypedQuery.getResultList();

    if ( balanceList.isEmpty() ) {
      return Optional.empty();
    }

    final Balance newestBalance = balanceList.get( 0 );

    return Optional.of( newestBalance );
  }

  /**
   * Updates the given {@link BalanceDTO} to the new balance it has after depositing or withdrawing money.
   *
   * @param oldBalance     The {@link Balance} displaying the current balance
   * @param updatingAmount The amount to be deposited or withdrawn
   *
   * @return The new balance with the current amount after depositing the money
   */
  private BalanceDTO createUpdatedBalance( Balance oldBalance, BigDecimal updatingAmount, boolean addition )
  {
    final BigDecimal currentAmount = oldBalance.getAmount();

    BigDecimal newAmount;

    if ( addition ) {
      newAmount = currentAmount.add( updatingAmount );
    }
    else {
      newAmount = currentAmount.subtract( updatingAmount );
    }

    final BankAccountDTO bankAccountDTO = new BankAccountDTO( oldBalance.getBankAccount() );
    final BalanceDTO newBalance = new BalanceDTO();

    newBalance.setCurrency( oldBalance.getCurrency() );
    newBalance.setBankAccountDTO( bankAccountDTO );
    newBalance.setType( BalanceType.AVAILABLE );
    newBalance.setAmount( newAmount );

    return newBalance;
  }

}
