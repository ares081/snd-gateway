package com.ares.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicRouteConfig {

  @Bean
  public NacosRouteDefinitionRepository nacosRouteDefinitionRepository() {
    return new NacosRouteDefinitionRepository();
  }
}
