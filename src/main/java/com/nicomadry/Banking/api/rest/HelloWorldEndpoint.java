package com.nicomadry.Banking.api.rest;

import com.nicomadry.Banking.api.repo.UserRepository;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@ApplicationScoped
@Path("/hello")
public class HelloWorldEndpoint {

  private UserRepository userRepository;
  private Logger log;

  @Inject
  public void init(UserRepository userRepository, Logger log)
  {
    this.userRepository = userRepository;
    this.log = log;
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
    log.info("Received user request");
    return Response.ok(userRepository.findAllOrderedByName()).build();
  }
}
