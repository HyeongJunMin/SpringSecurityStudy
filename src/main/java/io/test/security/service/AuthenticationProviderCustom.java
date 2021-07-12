package io.test.security.service;

import io.test.security.common.FormWebAuthenticationDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class AuthenticationProviderCustom implements AuthenticationProvider {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = (String) authentication.getCredentials();

    AccountContext accountContext  = (AccountContext) userDetailsService.loadUserByUsername(username);
    if (!passwordEncoder.matches(password, accountContext.getAccount().getPassword())) {
      throw new BadCredentialsException("Invalid password");
    }

    FormWebAuthenticationDetails details = (FormWebAuthenticationDetails) authentication.getDetails();
    String secretKey = details.getSecretKey();
    if (secretKey == null || !"secret".equals(secretKey)) {
      throw new InsufficientAuthenticationException("secret key does not match");
    }

    return new UsernamePasswordAuthenticationToken(accountContext.getAccount(), null, accountContext.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    //이 Provider는 UsernamePasswordAuthenticationToken 클래스에 대해 인증처리를 지원한다는 내용을 표현
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

}
