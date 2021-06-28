package io.test.security.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  @GetMapping("/user")
  public String get() {
    return "user!! : " + SecurityContextHolder.getContext().getAuthentication();
  }

}
