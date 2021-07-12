package io.test.security.config;

import io.test.security.common.FormAuthenticationDetailsSource;
import io.test.security.handler.AccessDeniedHandlerCustom;
import io.test.security.handler.AuthenticationFailureHandlerCustom;
import io.test.security.handler.AuthenticationSuccessHandlerCustom;
import io.test.security.service.AuthenticationProviderCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final String[] IGNORED_MATCHER_PATTERNS = {"/static/**", "/css/**", "/js/**", "/static/css/images/**", "/webjars/**", "/**/favicon.ico"};
  private final FormAuthenticationDetailsSource authenticationDetailsSource;
  private final AuthenticationSuccessHandler authenticationSuccessHandler;
  private final AuthenticationFailureHandler authenticationFailureHandler;
  private final AccessDeniedHandler accessDeniedHandler;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // auth.userDetailsService(userDetailsService); -- authenticationProvider에서 가져오므로 더 이상 필요없음
    auth.authenticationProvider(authenticationProvider());
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    return new AuthenticationProviderCustom();
  }

  //  @Override
//  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    auth.inMemoryAuthentication().withUser("user").password("1111").roles("USER");
//    auth.inMemoryAuthentication().withUser("sys").password("1111").roles("SYS");
//    auth.inMemoryAuthentication().withUser("admin").password("1111").roles("ADMIN");
//  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
          .antMatchers("/", "/users").permitAll()
          .antMatchers("/mypage").hasRole("USER")
          .antMatchers("/messaged").hasRole("MANAGER")
          .antMatchers("/admin").hasRole("ADMIN")
          .anyRequest().authenticated()
        .and()
        .formLogin()
          .loginPage("/login")
          .loginProcessingUrl("/login_proc")
          .defaultSuccessUrl("/")
          .successHandler(authenticationSuccessHandler)
          .failureHandler(authenticationFailureHandler)
          .authenticationDetailsSource(authenticationDetailsSource)
          .permitAll()
        .and()
        .exceptionHandling()
          .accessDeniedHandler(accessDeniedHandler)
    ;
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(IGNORED_MATCHER_PATTERNS);
  }

}
