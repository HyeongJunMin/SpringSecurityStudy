package io.test.security.repository;

import io.test.security.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Account, String> {

  Account findByUsername(String username);

}
