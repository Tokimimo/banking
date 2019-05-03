package com.nicomadry.Banking.itl.service.provider;

import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * This provider helps with the injection of a {@link Logger} which directly retrieves the name of the class it is injected in
 */
@ApplicationScoped
public class LoggerProvider {

  @Produces
  @Dependent
  public Logger provideLogger( @NotNull InjectionPoint injectionPoint )
  {
    return Logger.getLogger( injectionPoint.getMember().getDeclaringClass().getName() );
  }

}
