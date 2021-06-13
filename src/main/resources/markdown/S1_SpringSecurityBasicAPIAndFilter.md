# 섹션1 스프링 시큐리티 기본 API 및 Filter 이해

### 1. spring-boot-starter-security 디펜던시를 추가하면 무슨일이 생기나
1. 서버가 기동되면 스프링 시큐리티의 초기화 작업 및 기본적인 보안 설정이 이루어진다.
    - 기본적인 보안 설정?
        - 모든 요청에 대해 인증을 요구한다.
        - 인증 방식은 폼 로그인 방식과 httpBasic로그인 방식을 제공한다.
        - 기본 로그인 페이지 제공
        - 기본 계정 제공 : user / {password}
```
Using generated security password: 4add4fb7-0180-4223-8cde-a278183cb58c
```

### 2. 사용자 정의 보안 기능 구현(Customizing)
1. WebSecurityConfigurerAdapter
  - 서버가 구동되면서 이 클래스를 호출한다.
  - 스프링 시큐리티의 웹 보안 기능을 초기화하고 설정해주는 추상 클래스
  - HttpSecurity를 생성한다.(WebSecurityConfigurerAdapter의 init메서드에서)
  - HttpSecrity는 세부적인 보안 기능을 설정할 수 있는 API를 제공한다. 크게 인증 API, 인가 API로 나뉜다.
  - ```
    // WebSecurityConfigurerAdapter가 갖는 메서드
    @Override
    public void init(WebSecurity web) throws Exception {
      HttpSecurity http = getHttp();
      web.addSecurityFilterChainBuilder(http).postBuildAction(() -> {
        FilterSecurityInterceptor securityInterceptor = http.getSharedObject(FilterSecurityInterceptor.class);
        web.securityInterceptor(securityInterceptor);
      });
    }
    protected final HttpSecurity getHttp() throws Exception {
      if (this.http != null) {
        return this.http;
      }
      // ... 생략
      this.http = new HttpSecurity(this.objectPostProcessor, this.authenticationBuilder, sharedObjects);
      // ... 생략
      return this.http;
    }
    ```
2. 커스터마이징
    - @EnableWebSecurity 어노테이션을 custom config클래스에 선언해주어야 한다. : 웹 보안을 활성시키기 위한 어노테이션
3. FormLogin custom 설정
    - ```
      http
          .formLogin()
            .loginPage("/login") // 사용자 정의 로그인 페이지
            .defaultSuccessUrl("/home") // 로그인 성공 후 이동 페이지
            .failureUrl("/login?error=true") // 로그인 실패 후 이동 페이지
            .usernameParameter("username") // form에 아이디 파라미터 key
            .passwordParameter("password") // form에 비밀번호 파라미터 key
            .loginProcessingUrl("") // 로그인 Form action URL
            .successHandler(loginSuccessHandler()) // 로그인 성공 후 핸들러
            .failureHandler(loginFailureHandler()) // 로그인 실패 후 핸들러
      ```
4. UsernamePasswordAuthenticationFilter
    - username과 password로 인증을 처리하는 필터
    - AbstractAuthenticationProcessingFilter를 상속받고 있음(doFilter메서드는 부모클래스에)
    - 동작
      1. request의 요청 정보가 설정된 값(loginProcessingUrl)과 같으면 동작, 아니면 다음 필터로(chain.doFilter)
      2. Authentication 객체 생성(username + password)
      3. AuthenticationManager로 인증객체 전달(Authentication Manager 내부에 갖고있는 AuthenticationProvider 객체에 인증을 위임)
      4. ProviderManager가 provider를 찾아서 인증처리
      5. AuthenticationProvider에서 인증 실패하면 예외 발생, 성공하면 인증정보를 Authentication객체에 담아 manager로 리턴
      6. 여기까지 오면 Authentication 객체는 유저 정보와 권한들을 가짐
      7. 인증객체를 SecurityContext에 저장
      8. SuccessHandler 동작
5. LogoutFilter
    - 로그아웃(세션 무효화, 인증토큰 삭제, 쿠키정보 삭제 등) 처리해주는 필터
    - logoutHandler : 위 작업들을 처리해주는 핸들러. 추가 기능이 필요한 경우 커스텀 가능
    - 동작
      1. request의 요청 정보가 설정된 값(logoutUrl)과 같으면 동작, 아니면 다음 필터로(chain.doFilter)
      2. SecurityContext의 인증정보를 가져와서 로그아웃 핸들러로 보냄
      3. 로그아웃 핸들러는(여러 핸들러 공존 가능) 세션 무효화, 쿠키 삭제, 인증토큰 삭제, 컨텍스트 클리어 등 동작
6. RememberMeAuthenticationFilter
    - 서버가 쿠키를 발급해주고 클라이언트가 쿠키를 서버로 올렸을 때 서버는 토큰기반 인증을 사용해 검증이 되면 로그인 처리
    - 인증 성공할 때 쿠키 생성, 인증 실패 또는 로그아웃할 때 쿠키 삭제
    - 동작
      1. authentication 객체가 null일 때만 동작(null이 아니면 다음 필터로)
      2. RememberMeService에서 인증처리(구현체에 따라 remember-me 정보 메모리에 저장할지 DB에 저장할지 등 선택)
      3. 토큰이 있으면 추출, 없으면 doFilter
      4. 토큰을 decode해서 토큰이 서로 일치하는지, User 계정이 존재하는지를 검증하고 인증처리
      5. 4단계에서 문제 있으면 예외 발생
7. AnonymousAuthenticationFilter
    - 익명사용자 인증 처리 필터. Authentication 객체에 anonymousUser, ROLE_ANONYMOUS로 설정해준다.
    - 인증여부를 구현할 때 isAnonymous, isAuthenticated로 구분해서 사용한다. : AuthenticationTrustResolver
8. SessionManagementFilter
    - 세션 관리 필터(등록, 조회, 삭제, 이력관리, 동시세션제어, 세션고정보호, 세션생성정책결정)
    - 세션고정보호
        - 공격 시나리오 : 공격자가 사용자의 세션ID(쿠키)를 공격자의 세션 ID로 설정해서 로그인한 것 처럼 사용
        - 보호방법 : 인증할 때 마다 새 세션 ID 생성(changeSessionId)
        - 서블릿 3.1이전 : 이전 세션 값 가져와서 생성(migrateSession), 전혀 새로운 세션 생성(newSession)
        - 인증된 세션 ID를 탈취하는 공격에 대해서는 다르게 방어해야함(ex_ IP check)
    - 세션인증전략 순서(AuthenticationStrategy)
        - CompositeSessionAuthenticationStrategy가 delegateStrategies로 관리한다.
        - HttpSecurity(extends AbstractConfiguredSecurityBuilder).doBuild 에서 1~3은 init(), 4는 configure()에서 설정되기 때문에 아래와 같은 순서를 갖는다.  
        1. ConcurrentSessionControlAuthenticationStrategy : 동시세션 제어
        2. ChangeSessionIdAuthenticationStrategy : 세션고정보호(세션 발급)
        3. RegisterSessionAuthenticationStrategy : sessionRegistry에 세션정보를 등록하는 역할
        4. CsrfAuthenticationStrategy : CSRF Token 체크
        5. 위 전략 외 필요에 따라 다른 것들이 더 추가될 수 있음
9. ConcurrentSessionFilter
    - 동시세션 제어 필터
    - maximumSessions 설정이 있을 때만 필터체인에 등록됨 (SessionManagementConfigurer에 maximumSessions가 null이 아닌 경우)
10. 권한설정과 표현식(인가)
    - 권한설정 방식
        - 선언적 방식
            1. URL : 특정 URL마다 접근권한 지정
            2. 메서드 : 특정 메서드마다 접근권한 지정
    - URL 선언적 방식에서 표현식
        - antMatchers에 URL선언은 순서를 갖는다.
        - ```
          hasRole(String role)    // configurer에서 ROLE_ 붙여줌
          hasAnyRole(String... roles) // configurer에서 ROLE_ 붙여줌
          hasAuthority(String authority)
          hasAnyAuthority(String... authorities)
          access(String attribute)    //위 표현식들이 호출하는 메서드 
          hasIpAddress(String ipaddressExpression)
          permitAll()
          denyAll()
          anonymous()     // 익명 사용자 허용
          rememberMe()    // rememberMe 허용
          authenticated()
          fullyAuthenticated()    // rememberMe 제외하고 인증된 사용자 허용
          ```
11. ExceptionTranslationFilter
    - 인증예외 처리 필터이며 FilterSecurityInterceptor가 던지는 예외를 잡아서 호출함 
    - 처리하는 예외 종류
        - AuthenticationException : 인증 예외, AuthenticationEntryPoint 호출
        - AccessDeniedException : 인가 예외, AccessDeniedHandler에서 예외 처리
    - HttpSessionRequestCache        
        - 인증 예외가 발생한 경우 request정보를 담고있는 객체(세션에 저장됨)
        - ExceptionTranslationFilter에서 entryPoint.commence 메서드 호출하기 전 저장한다.(session.setAttribute)
        - RequestCacheAwareFilter에서 가져온다
        - 인증 예외 발생 후 다시 로그인에 성공했을 때 loginSuccessHandler에서 예외 발생 당시 requestUrl로 쉽게 리다이렉트 시켜줄 수 있다.
12. 사이트 간 요청 위조(CSRF)
    - 피해자의 의도와 무관하게 공격자가 의도한 행위를 서버에 요청하게끔 만드는 공격
    - CsrfFilter
        - 모든 요청에 랜덤하게 생성된 토큰을 http 파라미터로 요구
        - 서버가 내린 csrf 토큰과 일치하지 않으면 요청실패
        - XSS공격과 혼용돼서 csrf토큰까지 탈취되면 의미없음



*** 분명 Filter인데 chain.doFilter를 못찾은 경험이 있다. 그럴 땐 상속클래스를 확인해보면 된다.


*** 중간에 있는 eventPublisher는 어떻게 동작하나? 필터체인 중간에 끼게할 수 있나?