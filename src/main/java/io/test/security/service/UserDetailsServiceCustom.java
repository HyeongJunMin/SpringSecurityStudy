package io.test.security.service;

import io.test.security.domain.Account;
import io.test.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceCustom implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = userRepository.findByUsername(username);
    if (account == null) {
      throw new UsernameNotFoundException("No user found with username: " + username);
    }

    List<GrantedAuthority> roleList = new ArrayList();
    roleList.add(new SimpleGrantedAuthority("ROLE_USER") );

    return new AccountContext(account, roleList);
  }

}
