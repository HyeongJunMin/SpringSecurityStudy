package io.test.security.user;

import io.test.security.domain.Account;
import io.test.security.domain.vo.AccountDTO;
import io.test.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

  private final PasswordEncoder passwordEncoder;
  private final UserService userService;

  @GetMapping("/users")
  public String createUser() {
    return "login/register";
  }
  @PostMapping("/users")
  public String createUser(AccountDTO accountDTO) {
    Account account = Account.newInstance(accountDTO);
    account.setPassword(passwordEncoder.encode(account.getPassword()));
    userService.createUser(account);
    return "redirect:/";
  }

}
