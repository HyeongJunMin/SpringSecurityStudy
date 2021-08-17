package io.test.security.config;

import io.test.security.common.FormAuthenticationDetailsSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final String[] IGNORED_MATCHER_PATTERNS = {"/static/**", "/css/**", "/js/**", "/static/css/images/**", "/webjars/**", "/**/favicon.ico"};
  private final FormAuthenticationDetailsSource authenticationDetailsSource;
  private final AuthenticationSuccessHandler authenticationSuccessHandler;
  private final AuthenticationFailureHandler authenticationFailureHandler;
  private final AccessDeniedHandler accessDeniedHandler;

//  @Override
//  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    // auth.userDetailsService(userDetailsService); -- authenticationProvider에서 가져오므로 더 이상 필요없음
//    auth.authenticationProvider(authenticationProvider());
//  }

//  @Bean
//  public AuthenticationProvider authenticationProvider() {
//    return new AuthenticationProviderCustom();
//  }

  //  @Override
//  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    auth.inMemoryAuthentication().withUser("user").password("1111").roles("USER");
//    auth.inMemoryAuthentication().withUser("sys").password("1111").roles("SYS");
//    auth.inMemoryAuthentication().withUser("admin").password("1111").roles("ADMIN");
//  }

//  @Bean
//  public PasswordEncoder passwordEncoder() {
//    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//  }
//
//  @Override
//  protected void configure(HttpSecurity http) throws Exception {
//    http
//        .authorizeRequests()
//          .antMatchers("/", "/users").permitAll()
//          .antMatchers("/mypage").hasRole("USER")
//          .antMatchers("/messaged").hasRole("MANAGER")
//          .antMatchers("/admin").hasRole("ADMIN")
//          .anyRequest().authenticated()
//        .and()
//        .formLogin()
//          .loginPage("/login")
//          .loginProcessingUrl("/login_proc")
//          .defaultSuccessUrl("/")
//          .successHandler(authenticationSuccessHandler)
//          .failureHandler(authenticationFailureHandler)
//          .authenticationDetailsSource(authenticationDetailsSource)
//          .permitAll()
//        .and()
//        .exceptionHandling()
//          .accessDeniedHandler(accessDeniedHandler)
//        .and()
//          .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
//        .csrf().disable()
//    ;
//  }
//
//  @Override
//  public void configure(WebSecurity web) throws Exception {
//    web.ignoring().antMatchers(IGNORED_MATCHER_PATTERNS);
//  }
//
//  @Bean
//  public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
//    AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter();
//    filter.setAuthenticationManager(authenticationManagerBean());
//    return filter;
//  }

}
