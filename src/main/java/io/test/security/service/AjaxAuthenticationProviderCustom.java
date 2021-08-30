package io.test.security.service;

import io.test.security.token.AjaxAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

public class AjaxAuthenticationProviderCustom implements AuthenticationProvider {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String loginId = (String) authentication.getPrincipal();
    String password = (String) authentication.getCredentials();
    AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(loginId);
    if (!passwordEncoder.matches(password, accountContext.getPassword())) {
      throw new BadCredentialsException("Invalid Password");
    }
    return new AjaxAuthenticationToken(accountContext.getAccount(), null, accountContext.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> aClass) {
    return aClass.equals(AjaxAuthenticationToken.class);
  }

}