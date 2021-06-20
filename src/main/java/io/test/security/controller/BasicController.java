package io.test.security.controller;

import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class BasicController {

  @GetMapping("/home")
  public String home() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    authentication.isAuthenticated();
    boolean isAnonymous = authentication instanceof AnonymousAuthenticationProvider;
    return "home";
  }

  @GetMapping("/security1")
  public String needAuthorityUrl() {
    return "ok";
  }

  @GetMapping("/security2")
  public String permitAllUrl() {
    return "ok";
  }

  @GetMapping("/context-test")
  public String securityContextTest(HttpSession session) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    authentication.isAuthenticated();
    SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    Authentication authenticationFromSession = null;
    if (securityContext != null) {
      authenticationFromSession = securityContext.getAuthentication();
    }
    return "ok";
  }

  @GetMapping("/context-test/child-thread")
  public String securityContextChildThreadTest() {
    new Thread(() -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    }).start();
    return "thread";
  }

}
