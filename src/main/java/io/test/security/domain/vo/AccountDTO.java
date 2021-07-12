package io.test.security.domain.vo;

import lombok.Data;

@Data
public class AccountDTO {

  private String username;
  private String password;
  private String email;
  private String age;
  private String role;

}
