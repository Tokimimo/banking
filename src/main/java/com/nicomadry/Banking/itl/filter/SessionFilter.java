package com.nicomadry.Banking.itl.filter;

import com.nicomadry.Banking.itl.data.authn.AuthElement;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/*"})
@ApplicationScoped
public class SessionFilter implements Filter {

  private Logger log;

  @Inject
  public void init(Logger log)
  {
    this.log = log;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException
  {
    // Do nothing
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
  {
    final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

    // The received AuthToken to validate
    final String authToken = httpServletRequest.getHeader(AuthElement.PARAM_AUTH_TOKEN);
    final String address = getAddress(httpServletRequest);
    log.info("Received Request from: " + address + " with requested URI: " + httpServletRequest.getRequestURI());

    // TODO: Add check for active session
    filterChain.doFilter(servletRequest, httpServletResponse);
  }

  @Override
  public void destroy()
  {
    // Nothing to do here
  }

  /**
   * Gets the address.
   *
   * @param httpServletRequest the http servlet request
   * @return the address
   */
  private static String getAddress(final HttpServletRequest httpServletRequest)
  {
    String address = httpServletRequest.getHeader("X-FORWARDED-FOR");

    if (address == null) {
      address = httpServletRequest.getRemoteAddr();
    }

    return address;
  }
}
