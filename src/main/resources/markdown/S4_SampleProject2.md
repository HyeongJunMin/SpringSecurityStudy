# 섹션4 실전 프로젝트(Ajax 인증 구현)

### 1. 흐름 및 개요
![AuthenticationDetailsSource](../images/s4_1.png)

### 2. 인증 필터(AjaxAuthenticationFilter)
```
필터 구현 및 추가 작업 순서
   - AbstractAuthenticationProcessingFilter 상속
   - "/api/login" url로 Ajax 요청이 들어오는 경우 필터가 작동하도록 구현
   - AjaxAuthenticationToken을 AuthenticationManager에 전달해서 인증처리
   - 구현한 필터를 추가
```
1. 필터 구현
    - AjaxLoginProcessingFilter
2. 필터 추가
    - addFilter : this.filterOrders.getOrder(filter.getClass())의 order에 맞게 지정되며, order가 없으면 예외발생
    - addFilterAt : 지정된 필터의 순서에 넣어주지만 동일한 순서를 갖는 필터들은 서로 순서가 없고 override 하지 않는다.
    - addFilterBefore : 특정 필터 전에 필터 추가
    - addFilterAfter : 특정 필터 이후에 필터 추가
    - ```
      @Override
      protected void configure(HttpSecurity http) throws Exception {
        http
         ...
         .and()
          .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
      }    
      @Bean
      public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() {
        AjaxLoginProcessingFilter filter = new AjaxLoginProcessingFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
      }
      ```
3. AuthenticationProvider 구현 및 추가
    - service.AjaxAuthenticationProviderCustom