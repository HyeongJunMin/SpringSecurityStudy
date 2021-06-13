package io.test.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/shop")
public class ShopController {

  @GetMapping("/login")
  public String login() {
    return "login";
  }

}
