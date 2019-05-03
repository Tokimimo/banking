package com.nicomadry.Banking.itl.service.provider;

import com.nicomadry.Banking.api.data.annotation.SystemUTC;
import com.nicomadry.Banking.api.data.annotation.SystemZone;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.time.Clock;

/**
 * This provider helps with the injection of a {@link Clock} with correct timezone settings.
 */
@ApplicationScoped
public class ClockProvider {

  @Produces
  @SystemZone
  @Dependent
  public Clock getSystemDefaultClock()
  {
    return Clock.systemDefaultZone();
  }

  @Produces
  @SystemUTC
  @Dependent
  public Clock getUTCClock()
  {
    return Clock.systemUTC();
  }

}
