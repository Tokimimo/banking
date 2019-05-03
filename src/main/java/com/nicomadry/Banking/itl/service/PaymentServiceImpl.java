package com.nicomadry.Banking.itl.service;

import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.dto.BankAccountDTO;
import com.nicomadry.Banking.api.data.dto.PaymentDTO;
import com.nicomadry.Banking.api.data.entity.Balance;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.Payment;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.api.data.enums.BalanceType;
import com.nicomadry.Banking.api.data.enums.PaymentStatus;
import com.nicomadry.Banking.api.repo.BankAccountRepository;
import com.nicomadry.Banking.api.repo.PaymentRepository;
import com.nicomadry.Banking.api.repo.UserRepository;
import com.nicomadry.Banking.api.service.*;
import com.nicomadry.Banking.itl.exception.*;
import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Dependent
@Typed( PaymentService.class )
public class PaymentServiceImpl implements PaymentService {

  private static final String BLANK_IBAN = "DE00000000000000000000";

  private PaymentRepository paymentRepository;
  private UserRepository userRepository;
  private BankAccountRepository bankAccountRepository;
  private BankAccountService bankAccountService;
  private IBANService ibanService;
  private BalanceService balanceService;
  private CurrencyService currencyService;
  private Logger log;

  @Inject
  public void init( PaymentRepository paymentRepository, UserRepository userRepository, BankAccountRepository bankAccountRepository,
                    BankAccountService bankAccountService,
                    IBANService ibanService, BalanceService balanceService, CurrencyService currencyService, Logger log )
  {
    this.paymentRepository = paymentRepository;
    this.userRepository = userRepository;
    this.bankAccountRepository = bankAccountRepository;
    this.bankAccountService = bankAccountService;
    this.ibanService = ibanService;
    this.balanceService = balanceService;
    this.currencyService = currencyService;
    this.log = log;
  }

  private Payment createPaymentWithEmptyGiver( BankAccount bankAccount, User user,
                                               BigDecimal transaction ) throws NotCreatableException, NotFoundException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( user, "The user must not be null" );
    requireNonNull( transaction, "The transaction must not be null" );

    PaymentDTO paymentDTO = new PaymentDTO();
    paymentDTO.setAmount( transaction );
    paymentDTO.setCurrency( bankAccount.getAccountCurrency() );
    paymentDTO.setBeneficiaryAccountNumber( bankAccount.getAccountNumber() );
    paymentDTO.setBeneficiaryName( user.getUsername() );
    paymentDTO.setCreationDate( ZonedDateTime.now() );
    paymentDTO.setStatus( PaymentStatus.EXECUTED );
    paymentDTO.setGiverAccount( new BankAccountDTO( bankAccount ) );
    paymentDTO.setCommunication( "Money deposit" );

    return paymentRepository.createPayment( paymentDTO );
  }

  private Payment createPaymentWithEmptyReceiver( BankAccount bankAccount, User user,
                                                  BigDecimal transaction ) throws NotCreatableException, NotFoundException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( user, "The user must not be null" );
    requireNonNull( transaction, "The transaction must not be null" );

    PaymentDTO paymentDTO = new PaymentDTO();
    paymentDTO.setAmount( transaction );
    paymentDTO.setCurrency( bankAccount.getAccountCurrency() );
    paymentDTO.setBeneficiaryAccountNumber( BLANK_IBAN );
    paymentDTO.setGiverAccount( new BankAccountDTO( bankAccount ) );
    paymentDTO.setBeneficiaryName( user.getUsername() );
    paymentDTO.setCreationDate( ZonedDateTime.now() );
    paymentDTO.setStatus( PaymentStatus.EXECUTED );
    paymentDTO.setCommunication( "Money withdraw" );

    return paymentRepository.createPayment( paymentDTO );
  }

  @Override
  public Balance depositMoney( Long bankAccountId, Long userId,
                               BalanceDTO depositBalance ) throws NotFoundException, NotCreatableException, ValidationException
  {
    requireNonNull( bankAccountId, "The bankAccountId must not be null" );
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( depositBalance, "The balanceDTO must not be null" );

    final BankAccount bankAccount = getBankAccount( bankAccountId );

    return depositMoney( bankAccount, userId, depositBalance );
  }

  @Override
  public Balance depositMoney( BankAccount bankAccount, Long userId,
                               BalanceDTO depositBalance ) throws NotFoundException, NotCreatableException, ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( depositBalance, "The depositBalance must not be null" );

    final User user = getUser( userId );

    return depositMoney( bankAccount, user, depositBalance );
  }

  /**
   * Retrieves the {@link BankAccount} for the given ID.
   *
   * @param bankAccountId The ID of the {@link BankAccount}
   *
   * @return The found bank account
   *
   * @throws NotFoundException If no bank account for the was found
   */
  private BankAccount getBankAccount( Long bankAccountId ) throws NotFoundException
  {
    final Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( bankAccountId );

    return bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account found for ID: " + bankAccountId ) );
  }

  @NotNull
  private User getUser( Long userId ) throws NotFoundException
  {
    final Optional<User> userOptional = userRepository.findById( userId );
    return userOptional.orElseThrow( () -> new NotFoundException( "No user found for ID: " + userId ) );
  }

  @Override
  public Balance depositMoney( BankAccount bankAccount, User user,
                               BalanceDTO depositBalance ) throws NotCreatableException, ValidationException, NotFoundException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( user, "The user must not be null" );
    requireNonNull( depositBalance, "The depositBalance must not be null" );

    final List<User> accountUsers = bankAccountService.listUserOfAccount( bankAccount );

    if ( !accountUsers.contains( user ) ) {
      throw new ValidationException(
              "The user: [" + user.getUsername() + "] is not allowed to withdraw money from account: " + bankAccount.getAccountName() );
    }

    final Balance newBalance = balanceService.addBalance( bankAccount, depositBalance );

    BigDecimal depositAmount = currencyService.getCorrectMoneyAmount( bankAccount, depositBalance );

    createPaymentWithEmptyGiver( bankAccount, user, depositAmount );

    return newBalance;
  }

  @Override
  public Balance withdrawMoney( Long bankAccountId, Long userId,
                                BalanceDTO withdrawBalance ) throws NotFoundException, NotCreatableException, ValidationException
  {
    requireNonNull( bankAccountId, "The bankAccountId must not be null" );
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( withdrawBalance, "The balanceDTO must not be null" );

    final BankAccount bankAccount = getBankAccount( bankAccountId );

    return withdrawMoney( bankAccount, userId, withdrawBalance );
  }

  public Balance withdrawMoney( BankAccount bankAccount, Long userId,
                                BalanceDTO withdrawBalance ) throws NotFoundException, NotCreatableException, ValidationException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( withdrawBalance, "The withdrawBalance must not be null" );

    final User user = getUser( userId );

    return withdrawMoney( bankAccount, user, withdrawBalance );
  }

  @Override
  public Balance withdrawMoney( BankAccount bankAccount, User user,
                                BalanceDTO withdrawBalance ) throws NotCreatableException, ValidationException, NotFoundException
  {
    requireNonNull( bankAccount, "The bankAccount must not be null" );
    requireNonNull( user, "The user must not be null" );
    requireNonNull( withdrawBalance, "The withdrawBalance must not be null" );

    final List<User> accountUsers = bankAccountService.listUserOfAccount( bankAccount );

    if ( !accountUsers.contains( user ) ) {
      throw new ValidationException(
              "The user: [" + user.getUsername() + "] is not allowed to withdraw money from account: " + bankAccount.getAccountName() );
    }

    final Balance newBalance = balanceService.subtractBalance( bankAccount, withdrawBalance );

    BigDecimal withdrawAmount = currencyService.getCorrectMoneyAmount( bankAccount, withdrawBalance );

    createPaymentWithEmptyReceiver( bankAccount, user, withdrawAmount );

    return newBalance;
  }

  @Override
  public Payment performTransaction( Long giverBankAccountId, Long userId,
                                     PaymentDTO paymentDTO ) throws ValidationException, NotCreatableException, NotFoundException
  {
    requireNonNull( giverBankAccountId, "The giverBankAccountId must not be null" );
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( paymentDTO, "The paymentDTO must not be null" );

    final Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( giverBankAccountId );
    final BankAccount bankAccount = bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account found for ID: " + giverBankAccountId ) );

    return performTransaction( bankAccount, userId, paymentDTO );
  }

  @Override
  public Payment performTransaction( BankAccount giverBankAccount, Long userId,
                                     PaymentDTO paymentDTO ) throws ValidationException, NotCreatableException, NotFoundException
  {
    requireNonNull( giverBankAccount, "The bankAccount must not be null" );
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( paymentDTO, "The paymentDTO must not be null" );

    final Optional<User> userOptional = userRepository.findById( userId );
    final User user = userOptional.orElseThrow( () -> new NotFoundException( "No user found for ID: " + userId ) );

    return performTransaction( giverBankAccount, user, paymentDTO );
  }

  @Override
  public Payment performTransaction( BankAccount giverBankAccount, User user,
                                     PaymentDTO paymentDTO ) throws ValidationException, NotCreatableException, NotFoundException
  {
    requireNonNull( giverBankAccount, "The giverBankAccount must not be null" );
    requireNonNull( user, "The user must not be null" );
    requireNonNull( paymentDTO, "The paymentDTO must not be null" );

    final String beneficiaryIban = paymentDTO.getBeneficiaryAccountNumber();

    performIbanCheck( beneficiaryIban );

    if ( !giverBankAccount.getUsers().contains( user ) ) {
      throw new ValidationException(
              "The user: " + user.getUsername() + " is not allowed to perform a transaction with the account: " + giverBankAccount
                      .getAccountName() );
    }

    if ( giverBankAccount.getAccountNumber().equals( beneficiaryIban ) ) {
      throw new ValidationException( "Performing a transaction to the same account is forbidden" );
    }

    if ( giverBankAccount.getBlocked() ) {
      throw new ValidationException( "The account is blocked and currently cannot perform transaction" );
    }

    final BigDecimal paymentAmount = paymentDTO.getAmount();
    final String paymentCurrency = paymentDTO.getCurrency();

    currencyService.checkCurrencyValidity( paymentCurrency );

    final Optional<BigDecimal> exchangedAmountOptional = currencyService
            .getExchangedMoney( paymentCurrency, giverBankAccount.getAccountCurrency(), paymentAmount );

    final BigDecimal exchangedAmount = exchangedAmountOptional
            .orElseThrow( () -> new ValidationException( "Couldn't exchange the money to the correct currency" ) );

    checkForEnoughFunds( user, giverBankAccount, exchangedAmount );

    paymentDTO.setGiverAccount( new BankAccountDTO( giverBankAccount ) );

    final BalanceDTO paymentBalance = createBalanceForPayment( giverBankAccount, exchangedAmount );

    final Balance newGiverBalance = balanceService.subtractBalance( giverBankAccount, paymentBalance );
    log.debug( "The new balance for the giver is: " + newGiverBalance.getAmount() );

    final Optional<BankAccount> beneficiaryAccount = bankAccountService.getBankAccountByIban( beneficiaryIban );

    if ( beneficiaryAccount.isPresent() ) {
      final Balance newBeneficiaryBalance = balanceService.addBalance( beneficiaryAccount.get(), paymentBalance );
      log.debug( "The new balance if the beneficiary is: " + newBeneficiaryBalance.getAmount() );
    }

    return paymentRepository.createPayment( paymentDTO );
  }

  /**
   * Performs two checks for the given IBAN: <br/> 1. Checks if the IBAN is valid  <br/> 2. Checks if the IBAN is blacklisted
   *
   * @param iban The IBAN which will be checked
   *
   * @throws ValidationException If one of the checks fails
   */
  private void performIbanCheck( String iban ) throws ValidationException
  {
    try {
      final boolean ibanValid = ibanService.isIbanValid( iban );

      if ( !ibanValid ) {
        throw new ValidationException( "The target IBAN: [" + iban + "] is not a valid IBAN" );
      }
    }
    catch ( HTTPRequestException | DeserializationException e ) {
      throw new ValidationException( "Couldn't validate the IBAN: " + iban, e );
    }

    final boolean blacklisted = ibanService.isIbanBlacklisted( iban );

    if ( blacklisted ) {
      throw new ValidationException( "The target IBAN: [" + iban + "] is blacklisted. Aborting transaction" );
    }
  }

  /**
   * Creates a {@link BalanceDTO} representing the amount of the transaction.
   *
   * @param giverBankAccount  The {@link BankAccount} the transaction is made from
   * @param transactionAmount The amount of the transaction
   *
   * @return The created {@link BalanceDTO}
   */
  @NotNull
  private BalanceDTO createBalanceForPayment( BankAccount giverBankAccount, BigDecimal transactionAmount )
  {
    final BalanceDTO paymentBalance = new BalanceDTO();
    paymentBalance.setType( BalanceType.AVAILABLE );
    paymentBalance.setAmount( transactionAmount );
    paymentBalance.setCurrency( giverBankAccount.getAccountCurrency() );
    return paymentBalance;
  }

  /**
   * Checks if the given {@link BankAccount} has enough funds to perform the transaction. The {@link User} is needed to check if the user is
   * allowed to retrieve the information.
   *
   * @param user             The {@link User} initiating the transaction
   * @param giverBankAccount The {@link BankAccount} the transaction is made from
   * @param amount           The amount which is needed for the transaction
   *
   * @throws ValidationException If the {@link User} is not allowed to retrieve this information <br/> If the funds of the {@link
   *                             BankAccount} are not enough
   */
  private void checkForEnoughFunds( User user, BankAccount giverBankAccount,
                                    BigDecimal amount ) throws ValidationException
  {
    final boolean enoughFunds = balanceService.hasBalanceEnoughFunds( giverBankAccount, user, amount );

    if ( !enoughFunds ) {
      throw new ValidationException(
              "The account: " + giverBankAccount.getAccountName() + " doesn't have enough funds to perform the transaction" );
    }
  }
}
