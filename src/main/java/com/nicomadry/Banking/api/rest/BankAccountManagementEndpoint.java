package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.data.annotation.Authenticated;
import com.nicomadry.Banking.api.data.dto.BankAccountDTO;
import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.repo.BankAccountRepository;
import com.nicomadry.Banking.api.service.BankAccountService;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path( "/bankAccount" )
@ApplicationScoped
@Transactional( value = REQUIRED, rollbackOn = Exception.class )
public class BankAccountManagementEndpoint {

  private BankAccountRepository bankAccountRepository;

  private BankAccountService bankAccountService;

  @Inject
  public void init( BankAccountRepository bankAccountRepository, BankAccountService bankAccountService )
  {
    this.bankAccountRepository = bankAccountRepository;
    this.bankAccountService = bankAccountService;
  }

  @GET
  @Path( "/list" )
  @Produces( APPLICATION_JSON )
  public Response listAccounts()
  {
    // TESTED AND WORKING
    final List<BankAccount> bankAccountList = bankAccountRepository.findAllOrderedByName();
    final List<BankAccountDTO> convertedBankAccounts = bankAccountList.stream().map( BankAccountDTO::new ).collect( toList() );

    return Response.ok( convertedBankAccounts ).build();
  }

  @POST
  @Path( "/create/{userId}" )
  @Produces( APPLICATION_JSON )
  @Consumes( APPLICATION_JSON )
  @Authenticated
  public Response createBankAccount( @PathParam( "userId" ) Long userId,
                                     BankAccountDTO bankAccountDTO ) throws NotCreatableException, NotFoundException
  {
    // TESTED AND WORKING
    requireNonNull( userId, "The userId must not be null" );
    requireNonNull( bankAccountDTO, "The bankAccountDTO must not be null" );

    final BankAccount createdAccount = bankAccountService.createBankAccount( userId, bankAccountDTO );
    final BankAccountDTO createdAccountDTO = new BankAccountDTO( createdAccount );

    return Response.ok( createdAccountDTO ).build();
  }

  @PUT
  @Path( "/update/{accountId}" )
  @Produces( APPLICATION_JSON )
  @Consumes( APPLICATION_JSON )
  @Authenticated
  public Response updateBankAccount( @PathParam( "accountId" ) Long accountId,
                                     BankAccountDTO bankAccountDTO ) throws NotUpdatableException, NotFoundException, ValidationException
  {
    // TESTED AND WORKING
    requireNonNull( accountId, "The accountId must not be null" );
    requireNonNull( bankAccountDTO, "The bankAccountDTO must not be null" );

    final BankAccount updatedAccount = bankAccountRepository.updateBankAccount( accountId, bankAccountDTO );
    final BankAccountDTO updatedAccountDTO = new BankAccountDTO( updatedAccount );

    return Response.ok( updatedAccountDTO ).build();
  }

  @DELETE
  @Path( "/delete/{accountId}" )
  @Produces( APPLICATION_JSON )
  @Consumes( APPLICATION_JSON )
  @Authenticated
  public Response deleteBankAccount( @PathParam( "accountId" ) Long accountId ) throws NotFoundException, NotDeletableException
  {
    // TESTED AND WORKING
    requireNonNull( accountId, "The accountId must not be null" );

    bankAccountRepository.deleteBankAccount( accountId );

    return Response.ok().build();
  }

  @POST
  @Path( "/addUser/{accountId}" )
  @Produces( APPLICATION_JSON )
  @Consumes( APPLICATION_JSON )
  @Authenticated
  public Response addUserToAccount( @PathParam( "accountId" ) Long accountId,
                                    UserDTO userDTO ) throws NotFoundException, NotUpdatableException
  {
    // TESTED AND WORKING
    requireNonNull( accountId, "The accountId must not be null" );
    requireNonNull( userDTO, "The userDTO must not be null" );

    final Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( accountId );
    final BankAccount bankAccount = bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account found for ID: " + accountId ) );

    bankAccountService.addUserToAccount( userDTO.getUsername(), bankAccount );

    return Response.ok().build();
  }

  @POST
  @Path( "/removeUser/{accountId}" )
  @Produces( APPLICATION_JSON )
  @Consumes( APPLICATION_JSON )
  @Authenticated
  public Response removeUserFromAccount( @PathParam( "accountId" ) Long accountId,
                                         UserDTO userDTO ) throws NotFoundException, NotUpdatableException
  {
    // TESTED AND WORKING
    requireNonNull( accountId, "The accountId must not be null" );
    requireNonNull( userDTO, "The userDTO must not be null" );

    final Optional<BankAccount> bankAccountOptional = bankAccountRepository.findById( accountId );
    final BankAccount bankAccount = bankAccountOptional
            .orElseThrow( () -> new NotFoundException( "No bank account found for ID: " + accountId ) );

    bankAccountService.removeUserFromAccount( userDTO.getUsername(), bankAccount );

    return Response.ok().build();
  }

}
