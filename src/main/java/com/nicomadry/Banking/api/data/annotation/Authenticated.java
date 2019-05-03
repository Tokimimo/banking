package com.nicomadry.Banking.api.data.annotation;

import com.nicomadry.Banking.itl.filter.AuthenticationFilter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation will be used to signal REST Points to require a valid authentication. ( authentication = Logged in user ) Authentication
 * will be validated by JWT (JSON Web Token) and the filter {@link AuthenticationFilter} which will intercept calls to methods annotated
 * with this.
 */
@javax.ws.rs.NameBinding
@Retention( RUNTIME )
@Target( {TYPE, METHOD} )
public @interface Authenticated {
}
