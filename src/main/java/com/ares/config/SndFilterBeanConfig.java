package com.ares.config;

import com.ares.auth.UserAuthAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class SndFilterBeanConfig {

  @Bean
  @Order(1)
  public GlobalExceptionHandler globalExceptionHandler() {
    return new GlobalExceptionHandler();
  }

  @Bean
  @Order(-100)
  public UserAuthAdapter userAuthAdapter() {
    return new UserAuthAdapter();
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public LoggingFilter requestLoggingFilter() {
    return new LoggingFilter();
  }

}
