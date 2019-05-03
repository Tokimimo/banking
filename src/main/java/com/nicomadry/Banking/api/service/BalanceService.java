package com.nicomadry.Banking.api.service;

import com.nicomadry.Banking.api.data.dto.BalanceDTO;
import com.nicomadry.Banking.api.data.entity.Balance;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.itl.exception.NotCreatableException;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * The Balance service is used to handle and work with {@link Balance Balances}.  It only is responsible to supply information's about the
 * Balance or add/subtract funds to the current balance. For handling logic like payments use the service {@link PaymentService}
 */
public interface BalanceService {

  /**
   * Retrieves the current {@link Balance} of the given {@link BankAccount}, after validation if the {@link User} is associated to the
   * account.
   *
   * @param bankAccountId The ID of the {@link BankAccount} to retrieve the balance of
   * @param userId        The ID of the {@link User} which asks for the balance
   *
   * @return An {@link Optional} containing the balance, if the account has a balance
   *
   * @throws NotFoundException   If the {@link BankAccount} or {@link User} couldn't be found
   * @throws ValidationException If the {@link User} is not allowed to retrieve the Balance
   */
  Optional<Balance> getCurrentBalance( Long bankAccountId, Long userId ) throws NotFoundException, ValidationException;

  /**
   * Retrieves the current {@link Balance} of the given {@link BankAccount}, after validation if the {@link User} is associated to the
   * account.
   *
   * @param bankAccount The {@link BankAccount} to retrieve the balance of
   * @param userId      The ID of the {@link User} which asks for the balance
   *
   * @return An {@link Optional} containing the balance, if the account has a balance
   *
   * @throws NotFoundException   If the {@link User} couldn't be found
   * @throws ValidationException If the {@link User} is not allowed to retrieve the Balance
   */
  Optional<Balance> getCurrentBalance( BankAccount bankAccount, Long userId ) throws NotFoundException, ValidationException;

  /**
   * Retrieves the current {@link Balance} of the given {@link BankAccount}, after validation if the {@link User} is associated to the
   * account.
   *
   * @param bankAccount The {@link BankAccount} to retrieve the balance of
   * @param user        The {@link User} which asks for the balance
   *
   * @return An {@link Optional} containing the balance, if the account has a balance
   *
   * @throws ValidationException If the {@link User} is not allowed to retrieve the Balance
   */
  Optional<Balance> getCurrentBalance( BankAccount bankAccount, User user ) throws ValidationException;

  /**
   * Checks if the current {@link Balance} of the given {@link BankAccount} has enough funds, which is calculated if the given amount is
   * smaller than the funds in the balance.
   *
   * @param bankAccountId The ID of the {@link BankAccount} the balance is checked for
   * @param userId        The ID of the {@link User} which asks for the check
   * @param amount        The amount which have to be in the {@link Balance}
   *
   * @return True, if the balance has more funds than the given amount
   *
   * @throws NotFoundException   If no {@link BankAccount} or {@link User} couldn't be found
   * @throws ValidationException If the {@link User} is not allowed to perform this check
   */
  boolean hasBalanceEnoughFunds( Long bankAccountId, Long userId, BigDecimal amount ) throws NotFoundException, ValidationException;

  /**
   * Checks if the current {@link Balance} of the given {@link BankAccount} has enough funds, which is calculated if the given amount is
   * smaller than the funds in the balance.
   *
   * @param bankAccount The {@link BankAccount} the balance is checked for
   * @param userId      The ID of the {@link User} which asks for the check
   * @param amount      The amount which have to be in the {@link Balance}
   *
   * @return True, if the balance has more funds than the given amount
   *
   * @throws NotFoundException   If no {@link BankAccount} or {@link User} couldn't be found
   * @throws ValidationException If the {@link User} is not allowed to perform this check
   */
  boolean hasBalanceEnoughFunds( BankAccount bankAccount, Long userId, BigDecimal amount ) throws NotFoundException, ValidationException;

  /**
   * Checks if the current {@link Balance} of the given {@link BankAccount} has enough funds, which is calculated if the given amount is
   * smaller than the funds in the balance.
   *
   * @param bankAccount The {@link BankAccount} the balance is checked for
   * @param user        The {@link User} which asks for the check
   * @param amount      The amount which have to be in the {@link Balance}
   *
   * @return True, if the balance has more funds than the given amount
   *
   * @throws ValidationException If the {@link User} is not allowed to perform this check
   */
  boolean hasBalanceEnoughFunds( BankAccount bankAccount, User user, BigDecimal amount ) throws ValidationException;

  /**
   * Removes money of the given {@link BalanceDTO amount} from the given {@link BankAccount}. If the currencies differ, they will be
   * exchanged to the currency defined by the account. <br/>
   *
   * @param bankAccount The {@link BankAccount} the money will be subtracted from
   * @param balanceDTO  The {@link BalanceDTO balance} to be subtract
   *
   * @return The new {@link Balance} of the account
   *
   * @throws NotCreatableException If the creation of the new {@link Balance} failed
   * @throws ValidationException   If the money couldn't be exchanged to the new currency
   */
  Balance subtractBalance( BankAccount bankAccount, BalanceDTO balanceDTO ) throws NotCreatableException, ValidationException;

  /**
   * Adds money of the given {@link BalanceDTO amount} to the given {@link BankAccount}. If the currencies differ, they will be exchanged to
   * the currency defined by the account. <br/>
   *
   * @param bankAccount The {@link BankAccount} the money will be added to
   * @param balanceDTO  The {@link BalanceDTO balance} to be add
   *
   * @return The new {@link Balance} of the account
   *
   * @throws NotCreatableException If the creation of the new {@link Balance} failed
   * @throws ValidationException   If the money couldn't be exchanged to the new currency
   */
  Balance addBalance( BankAccount bankAccount, BalanceDTO balanceDTO ) throws NotCreatableException, ValidationException;

}
