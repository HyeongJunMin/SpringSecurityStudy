package io.test.security.config;

import io.test.security.common.AjaxLoginAuthenticationEntryPointCustom;
import io.test.security.filter.AjaxLoginProcessingFilter;
import io.test.security.handler.AjaxAccessDeniedHandlerCustom;
import io.test.security.handler.AjaxAuthenticationFailureHandlerCustom;
import io.test.security.handler.AjaxAuthenticationSuccessHandlerCustom;
import io.test.security.service.AjaxAuthenticationProviderCustom;
import io.test.security.service.UserDetailsServiceCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(0)
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider());
    auth.inMemoryAuthentication()
        .withUser("user").password("1111").roles("USER").and()
        .withUser("sys").password("1111").roles("SYS").and()
        .withUser("admin").password("1111").roles("ADMIN");
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    return new AjaxAuthenticationProviderCustom();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .antMatcher("/api/**")
        .authorizeRequests()
        .antMatchers("/api/messages").hasRole("MANAGER")
        .anyRequest().authenticated()
        .and()
//        .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
        .csrf().disable()
    ;

    http
        .exceptionHandling()
        .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPointCustom())
        .accessDeniedHandler(new AjaxAccessDeniedHandlerCustom())
    ;

    ajaxConfigureCustom(http);

//    http
//        .formLogin()
//        .loginPage("/login")
//    ;
  }

  private void ajaxConfigureCustom(HttpSecurity http) throws Exception {
    http
        .apply(new AjaxLoginConfigurer<>())
        .successHandlerAjax(new AjaxAuthenticationSuccessHandlerCustom())
        .failureHandlerAjax(new AjaxAuthenticationFailureHandlerCustom())
        .setAuthenticationManager(authenticationManagerBean())
        .loginProcessingUrl("/api/login")
    ;
  }

//  @Bean
//  public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
//    AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter();
//    filter.setAuthenticationManager(authenticationManagerBean());
//    filter.setAuthenticationSuccessHandler(new AjaxAuthenticationSuccessHandlerCustom());
//    filter.setAuthenticationFailureHandler(new AjaxAuthenticationFailureHandlerCustom());
//    return filter;
//  }

}