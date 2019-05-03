package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.data.annotation.Authenticated;
import com.nicomadry.Banking.api.data.dto.BankAccountDTO;
import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.entity.BankAccount;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.api.repo.UserRepository;
import com.nicomadry.Banking.api.service.BankAccountService;
import com.nicomadry.Banking.itl.exception.NotFoundException;
import com.nicomadry.Banking.itl.exception.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static com.nicomadry.Banking.api.rest.UserManagementEndpoint.USER_API_URI;

@Path( USER_API_URI )
@ApplicationScoped
@Transactional( value = Transactional.TxType.REQUIRED, rollbackOn = Exception.class )
public class UserManagementEndpoint {

  static final String USER_API_URI = "/user";
  private static final String CREATE_USER_URI = "/create";
  private static final String UPDATE_USER_URI = "/update/{userId: [0-9]+}";
  private static final String DELETE_USER_URI = "/delete/{userId: [0-9]+}";
  private static final String LIST_USER_URI = "/list";
  private static final String LIST_ACCOUNTS_URI = "/listAccounts/{userId: [0-9]+}";

  private UserRepository userRepository;
  private BankAccountService bankAccountService;

  @Inject
  public void init( UserRepository userRepository, BankAccountService bankAccountService )
  {
    this.userRepository = userRepository;
    this.bankAccountService = bankAccountService;
  }

  @POST
  @Path( CREATE_USER_URI )
  @Consumes( MediaType.APPLICATION_JSON )
  @Produces( MediaType.APPLICATION_JSON )
  @Authenticated
  public Response createUser( UserDTO user ) throws NotUniqueException, NotCreatableException
  {
    // TESTED AND WORKING
    final User createdUser = userRepository.createUser( user );
    return Response.ok( new UserDTO( createdUser ) ).build();
  }

  @POST
  @Path( UPDATE_USER_URI )
  @Consumes( MediaType.APPLICATION_JSON )
  @Produces( MediaType.APPLICATION_JSON )
  @Authenticated
  public Response updateUser( @PathParam( "userId" ) Long id,
                              UserDTO userDTO ) throws NotUpdatableException, NotFoundException
  {
    // TESTED AND WORKING
    final User user = userRepository.updateUser( id, userDTO );
    final UserDTO updatedUser = new UserDTO( user );

    return Response.ok( updatedUser ).build();
  }

  @DELETE
  @Path( DELETE_USER_URI )
  @Consumes( MediaType.APPLICATION_JSON )
  @Produces( MediaType.APPLICATION_JSON )
  @Authenticated
  public Response deleteUser( @PathParam( "userId" ) Long userId ) throws NotFoundException, NotDeletableException
  {
    // TESTED AND WORKING
    userRepository.deleteUser( userId );
    return Response.ok().build();
  }

  @GET
  @Path( LIST_USER_URI )
  @Produces( MediaType.APPLICATION_JSON )
  @Authenticated
  public Response listUser()
  {
    // TESTED AND WORKING
    final List<User> allUsers = userRepository.findAllOrderedByName();
    final List<UserDTO> convertedUsers = allUsers.stream().map( UserDTO::new ).collect( Collectors.toList() );

    return Response.ok( convertedUsers ).build();
  }

  @GET
  @Path( LIST_ACCOUNTS_URI )
  @Produces( MediaType.APPLICATION_JSON )
  @Authenticated
  public Response listAccounts( @PathParam( "userId" ) Long userId ) throws NotFoundException
  {
    // TESTED AND WORKING
    final List<BankAccount> bankAccounts = bankAccountService.listAccountsOfUser( userId );
    final List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map( BankAccountDTO::new ).collect( Collectors.toList() );

    return Response.ok( bankAccountDTOS ).build();
  }

}
