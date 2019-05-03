package com.nicomadry.Banking.api.service;

import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.dto.PaymentDTO;
import com.nicomadry.Banking.api.data.entity.Balance;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.Payment;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.itl.exception.NotCreatableException;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.ValidationException;

import java.math.BigDecimal;

/**
 * The payment service is responsible to handle transactions of all kinds. A transaction can either be a money deposit or withdrawing at an
 * ATM or a payment between {@link BankAccount BankAccount's}.
 */
public interface PaymentService {

  /**
   * Performs a deposit of the given {@link BalanceDTO amount} as new funds to the given {@link BankAccount}. If the currencies differ, they
   * will be exchanged to the currency defined by the account. <br/> To keep a log of what happened a {@link Payment} of the transaction
   * will be created.
   *
   * @param bankAccountId  The ID of the {@link BankAccount} the money will be added to
   * @param userId         The ID of the {@link User} which is depositing the money
   * @param depositBalance The {@link BalanceDTO balance} to be deposited
   *
   * @return The new {@link Balance} of the account
   *
   * @throws NotFoundException     If no {@link BankAccount} could be found
   * @throws NotCreatableException If the creation of the new {@link Balance} failed
   * @throws ValidationException   If the money couldn't be exchanged to the new currency
   */
  Balance depositMoney( Long bankAccountId, Long userId,
                        BalanceDTO depositBalance ) throws NotFoundException, NotCreatableException, ValidationException;

  /**
   * Performs a deposit of the given {@link BalanceDTO amount} as new funds to the given {@link BankAccount}. If the currencies differ, they
   * will be exchanged to the currency defined by the account. <br/> To keep a log of what happened a {@link Payment} of the transaction
   * will be created.
   *
   * @param bankAccount    The {@link BankAccount} the money will be added to
   * @param userId         The ID of the {@link User} which is depositing the money
   * @param depositBalance The {@link BalanceDTO balance} to be deposited
   *
   * @return The new {@link Balance} of the account
   *
   * @throws NotFoundException     If no {@link BankAccount} could be found
   * @throws NotCreatableException If the creation of the new {@link Balance} failed
   * @throws ValidationException   If the money couldn't be exchanged to the new currency
   */
  Balance depositMoney( BankAccount bankAccount, Long userId,
                        BalanceDTO depositBalance ) throws NotFoundException, NotCreatableException, ValidationException;

  /**
   * Performs a deposit of the given {@link BalanceDTO amount} as new funds to the given {@link BankAccount}. If the currencies differ, they
   * will be exchanged to the currency defined by the account. <br/> *To keep a log of what happened a {@link Payment} of the transaction
   * will be created.
   *
   * @param bankAccount    The {@link BankAccount} the money will be added to
   * @param user           The {@link User} which is depositing the money
   * @param depositBalance The {@link BalanceDTO balance} to be deposited
   *
   * @return The new {@link Balance} of the account
   *
   * @throws NotCreatableException If the creation of the new {@link Balance} failed
   * @throws ValidationException   If the money couldn't be exchanged to the new currency
   */
  Balance depositMoney( BankAccount bankAccount, User user,
                        BalanceDTO depositBalance ) throws NotCreatableException, ValidationException, NotFoundException;

  /**
   * Performs a withdraw of the given {@link BalanceDTO amount} of money from the given {@link BankAccount}. If the currencies differ, they
   * will be exchanged to the currency defined by the account. <br/> To keep a log of what happened a {@link Payment} of the transaction
   * will be created.
   *
   * @param bankAccountId   The ID of the {@link BankAccount} the money will be taken from
   * @param userId          The ID of the {@link User} which is withdrawing the money
   * @param withdrawBalance The {@link BalanceDTO balance} to be withdrawn
   *
   * @return The new {@link Balance} of the account
   *
   * @throws NotFoundException     If no {@link BankAccount} could be found
   * @throws NotCreatableException If the creation of the new {@link Balance} failed
   * @throws ValidationException   If the money couldn't be exchanged to the new currency
   */
  Balance withdrawMoney( Long bankAccountId, Long userId,
                         BalanceDTO withdrawBalance ) throws NotFoundException, NotCreatableException, ValidationException;

  /**
   * Performs a withdraw of the given {@link BalanceDTO amount} of money from the given {@link BankAccount}. If the currencies differ, they
   * will be exchanged to the currency defined by the account. <br/> To keep a log of what happened a {@link Payment} of the transaction
   * will be created.
   *
   * @param bankAccount     The {@link BankAccount} the money will be taken from
   * @param userId          The ID of the {@link User} which is withdrawing the money
   * @param withdrawBalance The {@link BalanceDTO balance} to be withdrawn
   *
   * @return The new {@link Balance} of the account
   *
   * @throws NotFoundException     If no {@link BankAccount} could be found
   * @throws NotCreatableException If the creation of the new {@link Balance} failed
   * @throws ValidationException   If the money couldn't be exchanged to the new currency
   */
  Balance withdrawMoney( BankAccount bankAccount, Long userId,
                         BalanceDTO withdrawBalance ) throws NotFoundException, NotCreatableException, ValidationException;

  /**
   * Performs a withdraw of the given {@link BalanceDTO amount} of money from the given {@link BankAccount}. If the currencies differ, they
   * will be exchanged to the currency defined by the account. <br/> To keep a log of what happened a {@link Payment} of the transaction
   * will be created.
   *
   * @param bankAccount     The {@link BankAccount} the money will be taken from
   * @param user            The {@link User} which is withdrawing the money
   * @param withdrawBalance The {@link BalanceDTO balance} to be withdrawn
   *
   * @return The new {@link Balance} of the account
   *
   * @throws NotCreatableException If the creation of the new {@link Balance} failed
   * @throws ValidationException   If the money couldn't be exchanged to the new currency
   */
  Balance withdrawMoney( BankAccount bankAccount, User user,
                         BalanceDTO withdrawBalance ) throws NotCreatableException, ValidationException, NotFoundException;

  /**
   * Creates and performs the {@link Payment}, which will substract/add {@link Balance} to {@link BankAccount accounts} in the system
   * depending on the transaction being made.
   *
   * @param giverBankAccountId The ID of the {@link BankAccount} the transaction will be made from
   * @param userId             The ID of the {@link User} who performs the transaction
   * @param paymentDTO         The {@link PaymentDTO} to be done
   *
   * @return The created and finished {@link Payment}
   *
   * @throws ValidationException   If the IBAN is not valid or if the IBAN is blacklisted
   * @throws NotCreatableException If the Payment couldn't be persisted
   */
  Payment performTransaction( Long giverBankAccountId, Long userId,
                              PaymentDTO paymentDTO ) throws ValidationException, NotCreatableException, NotFoundException;

  /**
   * Creates and performs the {@link Payment}, which will substract/add {@link Balance} to {@link BankAccount accounts} in the system
   * depending on the transaction being made.
   *
   * @param giverBankAccount The {@link BankAccount} the transaction will be made from
   * @param userId           The ID of the {@link User} who performs the transaction
   * @param paymentDTO       The {@link PaymentDTO} to be done
   *
   * @return The created and finished {@link Payment}
   *
   * @throws ValidationException   If the IBAN is not valid or if the IBAN is blacklisted
   * @throws NotCreatableException If the Payment couldn't be persisted
   */
  Payment performTransaction( BankAccount giverBankAccount, Long userId,
                              PaymentDTO paymentDTO ) throws ValidationException, NotCreatableException, NotFoundException;

  /**
   * Creates and performs the {@link Payment}, which will substract/add {@link Balance} to {@link BankAccount accounts} in the system
   * depending on the transaction being made.
   *
   * @param giverBankAccount The {@link BankAccount} the transaction will be made from
   * @param user             The {@link User} who performs the transaction
   * @param paymentDTO       The {@link PaymentDTO} to be done
   *
   * @return The created and finished {@link Payment}
   *
   * @throws ValidationException   If the IBAN is not valid or if the IBAN is blacklisted
   * @throws NotCreatableException If the Payment couldn't be persisted
   */
  Payment performTransaction( BankAccount giverBankAccount, User user,
                              PaymentDTO paymentDTO ) throws ValidationException, NotCreatableException, NotFoundException;

}
