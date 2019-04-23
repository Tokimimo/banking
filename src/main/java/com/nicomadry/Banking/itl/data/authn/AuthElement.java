package com.nicomadry.Banking.itl.data.authn;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class AuthElement implements Serializable {
  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = -669163012857852L;

  /**
   * The Constant PARAM_AUTH_TOKEN.
   */
  public static final String PARAM_AUTH_TOKEN = "Auth-Token";

  /**
   * The auth token.
   */
  private String authToken;

  /**
   * The duration.
   */
  private Integer duration;

  public AuthElement(String authToken, Integer duration)
  {
    this.authToken = authToken;
    this.duration = duration;
  }

  public String getAuthToken()
  {
    return authToken;
  }

  public void setAuthToken(String authToken)
  {
    this.authToken = authToken;
  }

  public Integer getDuration()
  {
    return duration;
  }

  public void setDuration(Integer duration)
  {
    this.duration = duration;
  }
}
