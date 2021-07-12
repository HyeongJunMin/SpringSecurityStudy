package io.test.security.controller;

import io.test.security.domain.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

  @GetMapping("/login")
  public String login(@RequestParam(required = false) String error,
                      @RequestParam(required = false) String exception, Model model) throws Exception {
    model.addAttribute("error", error);
    model.addAttribute("exception", exception);
    return "login";
  }

  @GetMapping("/logout")
  public String logout(HttpServletRequest request, HttpServletResponse response) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    return "redirect:/login";
  }

  @GetMapping("/denied")
  public String denied(@RequestParam(required = false) String message, Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Account account = (Account) authentication.getPrincipal();
    model.addAttribute("username", account.getUsername());
    model.addAttribute("message", message);
    return "login/denied";
  }

}
