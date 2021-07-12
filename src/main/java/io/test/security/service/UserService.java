package io.test.security.service;

import io.test.security.domain.Account;
import io.test.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public void createUser(Account account) {
    userRepository.save(account);
  }

}
