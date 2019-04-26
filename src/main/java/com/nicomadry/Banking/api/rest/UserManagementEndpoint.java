package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.data.dto.UserDTO;
import com.nicomadry.Banking.api.data.entity.User;
import com.nicomadry.Banking.api.repo.UserRepository;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/userManagement")
@ApplicationScoped
public class UserManagementEndpoint {

  private Logger log;

  private UserRepository userRepository;

  @Inject
  public void init(Logger log, UserRepository userRepository)
  {
    this.log = log;
    this.userRepository = userRepository;
  }

  @POST
  @Path("/create")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createUser(UserDTO user) throws Exception
  {
    log.info("Persisting User: " + user.getUsername());

    userRepository.createUser(user);

    return Response.ok(user).build();
  }


}
