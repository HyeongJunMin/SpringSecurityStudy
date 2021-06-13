package io.test.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final String LOGIN_URL = "/login";

  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser("user").password("{noop}1111").roles("USER");
    auth.inMemoryAuthentication().withUser("sys").password("{noop}1111").roles("SYS");
    auth.inMemoryAuthentication().withUser("admin").password("{noop}1111").roles("ADMIN");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // 인가 정책
    // URL 선언적 방식
    http
        .authorizeRequests()
          .antMatchers("/login").permitAll()
          .antMatchers("/home/**").hasAnyRole("USER", "SYS", "ADMIN")
          .antMatchers("/user").hasRole("USER")
          .antMatchers("/admin/pay").hasRole("SYS")
          .antMatchers("/admin/**").hasAnyRole("SYS", "ADMIN")
          .anyRequest().authenticated()
    ;
    // 인증 정책
    http
        .formLogin()
//          .loginPage("/login") // 사용자 정의 로그인 페이지
          .defaultSuccessUrl("/home") // 로그인 성공 후 이동 페이지
//          .failureUrl("/login") // 로그인 실패 후 이동 페이지
          .usernameParameter("userId") // form에 아이디 파라미터 key
          .passwordParameter("password") // form에 비밀번호 파라미터 key
//          .loginProcessingUrl("/login") // 로그인 Form action URL
          .successHandler(loginSuccessHandler()) // 로그인 성공 후 핸들러
          .failureHandler(loginFailureHandler()) // 로그인 실패 후 핸들러
    ;
    // 로그아웃 관련 정책
    http
        .logout()
          .logoutUrl("/logout")
          .logoutSuccessUrl("/login")
          .addLogoutHandler((request, response, authentication) -> {
            HttpSession session = request.getSession();
            session.invalidate();
          })
          .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/login"))
          .deleteCookies("remember-me")
    ;
    // remember-me 설정과 default 값들
    http
        .rememberMe()
          .rememberMeParameter("remember-me")
          .tokenValiditySeconds(1_209_600)
          .alwaysRemember(false)
          .userDetailsService(userDetailsService)
    ;
    // 세션탈취방어
    http
        .sessionManagement()
          .sessionFixation().changeSessionId()
    .maximumSessions(1).maxSessionsPreventsLogin(true)
    ;
    // dPdhlcjfl
    http
        .exceptionHandling()
//          .authenticationEntryPoint(authenticationEntryPoint())
          .accessDeniedHandler(accessDeniedHandler())
    ;
  }

  private AuthenticationSuccessHandler loginSuccessHandler() {
    return (httpServletRequest, httpServletResponse, authentication) -> {
      System.out.println("authentication : " + authentication.getName());
      HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
      SavedRequest savedRequest = requestCache.getRequest(httpServletRequest, httpServletResponse);
      if (savedRequest != null) {
        System.out.println("saved url : " + savedRequest.getRedirectUrl());
        httpServletResponse.sendRedirect(savedRequest.getRedirectUrl());
      } else {
        httpServletResponse.sendRedirect("/home");
      }
    };
  }

  private AuthenticationFailureHandler loginFailureHandler() {
    return (httpServletRequest, httpServletResponse, e) -> {
      System.out.println("exception : " + e.getMessage());
//      httpServletResponse.sendRedirect("/login");
    };
  }

  private AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, authException) -> response.sendRedirect("/login");
  }

  private AccessDeniedHandler accessDeniedHandler() {
    return (request, response, accessDeniedException) -> response.sendRedirect("/denied");
  }

}