package com.nicomadry.Banking.rest;

import com.nicomadry.Banking.api.repo.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@ApplicationScoped
@Path("/hello")
public class HelloWorldEndpoint {

  private UserRepository userRepository;

  public void init(UserRepository userRepository)
  {
    this.userRepository = userRepository;
  }

  @GET
  @Produces("text/plain")
  public Response doGet()
  {
    return Response.ok("Hello from Nico Madry!").build();
  }

  @GET
  @Path("/user")
  @Produces(MediaType.APPLICATION_JSON)
  public Response listUser()
  {
    return Response.ok(userRepository.findAllOrderedByName()).build();
  }
}
