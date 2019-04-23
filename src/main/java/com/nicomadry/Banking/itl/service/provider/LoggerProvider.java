package com.nicomadry.Banking.itl.service.provider;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class LoggerProvider {

  @Produces
  @Dependent
  public Logger provideLogger(InjectionPoint injectionPoint)
  {
    return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
  }

}
