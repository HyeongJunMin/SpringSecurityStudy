package io.test.security.controller;

import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

  @GetMapping("/home")
  public String home() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    authentication.isAuthenticated();
    boolean isAnonymous = authentication instanceof AnonymousAuthenticationProvider;
    return "home";
  }

}
