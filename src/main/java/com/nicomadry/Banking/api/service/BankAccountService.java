package com.nicomadry.Banking.api.service;

import com.nicomadry.Banking.api.data.dto.BankAccountDTO;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.itl.exception.NotCreatableException;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.NotUpdatableException;

import java.util.List;
import java.util.Optional;

/**
 * The Bank account service is responsible for handling data related to a {@link BankAccount}.
 */
public interface BankAccountService {

  /**
   * Creates a new {@link BankAccount} for the given {@link User} which will be the initial account user.
   *
   * @param userId         The ID of the {@link User} the bank account will be created for
   * @param bankAccountDTO The data of the {@link BankAccount} to be created
   *
   * @return The new {@link BankAccount}
   *
   * @throws NotFoundException     If no {@link User} was found for the given ID
   * @throws NotCreatableException If the {@link BankAccount} couldn't be created because of the following reasons: <br/> - The entered IBAN
   *                               is invalid <br/> - If the entered account currency is invalid
   */
  BankAccount createBankAccount( Long userId, BankAccountDTO bankAccountDTO ) throws NotFoundException, NotCreatableException;

  /**
   * Creates a new {@link BankAccount} for the given {@link User} which will be the initial account user.
   *
   * @param user           The {@link User} the bank account will be created for
   * @param bankAccountDTO The data of the {@link BankAccount} to be created
   *
   * @return The new {@link BankAccount}
   *
   * @throws NotCreatableException If the {@link BankAccount} couldn't be created because of the following reasons: <br/> - The entered IBAN
   *                               is invalid <br/> - If the entered account currency is invalid
   */
  BankAccount createBankAccount( User user, BankAccountDTO bankAccountDTO ) throws NotCreatableException;

  /**
   * Adds a new {@link User} to the given {@link BankAccount}, which then will be able to perform transactions and retrieve informations for
   * the given account.
   *
   * @param userId      The ID of the {@link User} which will be added to the account
   * @param bankAccount The {@link BankAccount} which the user will be added to
   *
   * @return The updated {@link BankAccount}, which holds a new user as account user
   *
   * @throws NotFoundException     If no {@link User} was found for the given ID
   * @throws NotUpdatableException If the {@link User} couldn't be added to the account
   */
  BankAccount addUserToAccount( Long userId, BankAccount bankAccount ) throws NotFoundException, NotUpdatableException;

  /**
   * Adds a new {@link User} to the given {@link BankAccount}, which then will be able to perform transactions and retrieve informations for
   * the given account.
   *
   * @param username    The name of the {@link User} which will be added to the account
   * @param bankAccount The {@link BankAccount} which the user will be added to
   *
   * @return The updated {@link BankAccount}, which holds a new user as account user
   *
   * @throws NotFoundException     If no {@link User} was found for the given name
   * @throws NotUpdatableException If the {@link User} couldn't be added to the account
   */
  BankAccount addUserToAccount( String username, BankAccount bankAccount ) throws NotFoundException, NotUpdatableException;

  /**
   * Adds a new {@link User} to the given {@link BankAccount}, which then will be able to perform transactions and retrieve informations for
   * the given account.
   *
   * @param user        The {@link User} which will be added to the account
   * @param bankAccount The {@link BankAccount} which the user will be added to
   *
   * @return The updated {@link BankAccount}, which holds a new user as account user
   *
   * @throws NotUpdatableException If the {@link User} couldn't be added to the account
   */
  BankAccount addUserToAccount( User user, BankAccount bankAccount ) throws NotUpdatableException;

  /**
   * Removes the {@link User} from the given {@link BankAccount}, which then looses the access to perform transactions and retrieve
   * information's for the given account.
   *
   * @param userId      The ID of the {@link User} which will be removed to the account
   * @param bankAccount The {@link BankAccount} which the user will be removed from
   *
   * @return The updated {@link BankAccount}, which holds a user less as account user
   *
   * @throws NotFoundException     If no {@link User} was found for the given ID
   * @throws NotUpdatableException If the {@link User} couldn't be removed from the account
   */
  BankAccount removeUserFromAccount( Long userId, BankAccount bankAccount ) throws NotFoundException, NotUpdatableException;

  /**
   * Removes the {@link User} from the given {@link BankAccount}, which then looses the access to perform transactions and retrieve
   * information's for the given account.
   *
   * @param username    The name of the {@link User} which will be removed to the account
   * @param bankAccount The {@link BankAccount} which the user will be removed from
   *
   * @return The updated {@link BankAccount}, which holds a user less as account user
   *
   * @throws NotFoundException     If no {@link User} was found for the given name
   * @throws NotUpdatableException If the {@link User} couldn't be removed from the account
   */
  BankAccount removeUserFromAccount( String username, BankAccount bankAccount ) throws NotFoundException, NotUpdatableException;

  /**
   * Removes the {@link User} from the given {@link BankAccount}, which then looses the access to perform transactions and retrieve
   * information's for the given account.
   *
   * @param user        The {@link User} which will be removed to the account
   * @param bankAccount The {@link BankAccount} which the user will be removed from
   *
   * @return The updated {@link BankAccount}, which holds a user less as account user
   *
   * @throws NotUpdatableException If the {@link User} couldn't be removed from the account
   */
  BankAccount removeUserFromAccount( User user, BankAccount bankAccount ) throws NotUpdatableException;

  /**
   * Lists all {@link User User's} which are associated to the given {@link BankAccount}.
   *
   * @param bankAccountId The ID of the {@link BankAccount} to retrieve all users for
   *
   * @return The list of all associated {@link User User's}
   *
   * @throws NotFoundException If no {@link BankAccount} was found for the given ID
   */
  List<User> listUserOfAccount( Long bankAccountId ) throws NotFoundException;

  /**
   * Lists all {@link User User's} which are associated to the given {@link BankAccount}.
   *
   * @param bankAccount The {@link BankAccount} to retrieve all users for
   *
   * @return The list of all associated {@link User User's}
   */
  List<User> listUserOfAccount( BankAccount bankAccount );

  /**
   * Lists all {@link BankAccount BankAccount's} the given {@link User} is associated to.
   *
   * @param userId The ID of the {@link User} to retrieve all accounts for *    *
   *
   * @return The list of all associated {@link BankAccount BankAccount's}
   *
   * @throws NotFoundException If no {@link User} was found for the given ID
   */
  List<BankAccount> listAccountsOfUser( Long userId ) throws NotFoundException;

  /**
   * Lists all {@link BankAccount BankAccount's} the given {@link User} is associated to.
   *
   * @param user The {@link User} to retrieve all accounts for
   *
   * @return The list of all associated {@link BankAccount BankAccount's}
   */
  List<BankAccount> listAccountsOfUser( User user );

  /**
   * Retrieves the {@link BankAccount} for the informations given in the {@link BankAccountDTO}. <br/> This function is mostly useful for
   * REST endpoints.
   *
   * @param bankAccountDTO The {@link BankAccountDTO} holding all given informations of the {@link BankAccount}
   *
   * @return An {@link Optional} possibly containing the {@link BankAccount}
   */
  Optional<BankAccount> getBankAccount( BankAccountDTO bankAccountDTO );

  /**
   * Retrieves the {@link BankAccount} for the given IBAN.
   *
   * @param accountIban The IBAN of the {@link BankAccount}
   *
   * @return An {@link Optional} possibly containing the {@link BankAccount}
   */
  Optional<BankAccount> getBankAccountByIban( String accountIban );

  /**
   * Retrieves the {@link BankAccount} for the given account name
   *
   * @param accountName The name of the {@link BankAccount}
   *
   * @return An {@link Optional} possibly containing the {@link BankAccount}
   */
  Optional<BankAccount> getBankAccountByName( String accountName );
}
