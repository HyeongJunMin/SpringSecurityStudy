package io.test.security.domain;

import io.test.security.domain.vo.AccountDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.BeanUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class Account {

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid2")
  private String id;
  private String username;
  private String password;
  private String email;
  private String age;
  private String role;

  public static Account newInstance(AccountDTO dto) {
    Account account = new Account();
    BeanUtils.copyProperties(dto, account);
    return account;
  }

}
