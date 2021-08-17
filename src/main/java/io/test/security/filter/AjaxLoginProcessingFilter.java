package io.test.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.test.security.domain.vo.AccountDTO;
import io.test.security.token.AjaxAuthenticationToken;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

  private ObjectMapper objectMapper = new ObjectMapper();

  public AjaxLoginProcessingFilter() {
    super(new AntPathRequestMatcher("/api/login"));
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    // ajax 요청인지 확인
    if (!isAjax(request)) {
      throw new IllegalStateException("Authentication is not supported");
    }

    AccountDTO accountDTO = objectMapper.readValue(request.getReader(), AccountDTO.class);
    String username = accountDTO.getUsername();
    String password = accountDTO.getPassword();
    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
      throw new IllegalArgumentException(String.format("Username or Password is Empty. username : {}, password : {}"
          , username, password));
    }

    AjaxAuthenticationToken token = new AjaxAuthenticationToken(username, password);
    // 인증 처리
    return getAuthenticationManager().authenticate(token);
  }

  private boolean isAjax(HttpServletRequest request) {
    if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
      return true;
    }
    return false;
  }

}
