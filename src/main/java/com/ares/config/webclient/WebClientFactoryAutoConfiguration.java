package com.ares.config.webclient;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
@ConditionalOnClass(WebClient.class)
@EnableConfigurationProperties(CustomWebClientProperties.class)
@AutoConfigureAfter(WebClient.class)
public class WebClientFactoryAutoConfiguration {

  private final Logger logger = LoggerFactory.getLogger(WebClientFactoryAutoConfiguration.class);

  @Bean
  @LoadBalanced // Add this annotation for service discovery
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }

  /**
   * WebClient的配置类
   * 
   * @param webClientBuilder WebClient.Builder
   * @param properties 配置属性
   * @return CustomWebClientFactory
   */
  @Bean
  @LoadBalanced
  @ConditionalOnMissingBean
  CustomWebClientFactory customWebClientFactory(WebClient.Builder webClientBuilder,
      CustomWebClientProperties properties) {

    // 使用属性配置
    if (properties.getUrl() != null) {
      webClientBuilder.baseUrl(properties.getUrl());
    } else if (properties.getServerName() != null) {
      webClientBuilder.baseUrl(String.format("lb://%s", properties.getServerName()));
    }

    // 设置超时
    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout())
        .responseTimeout(Duration.ofMillis(properties.getReadTimeout()));

    webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));

    // 设置日志
    if (properties.isEnableLogging()) {
      webClientBuilder.filter((request, next) -> {
        logger.info("Request: {} {}", request.method(), request.url());
        return next.exchange(request);
      });
    }

    return new CustomWebClientFactory(webClientBuilder);
  }

}
