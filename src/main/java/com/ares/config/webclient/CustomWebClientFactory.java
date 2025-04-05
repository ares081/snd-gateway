package com.ares.config.webclient;

import org.springframework.web.reactive.function.client.WebClient;

public class CustomWebClientFactory {
  private final WebClient.Builder webClientBuilder;

  public CustomWebClientFactory(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  public WebClient createWebClient() {
    return webClientBuilder.build();
  }

  public WebClient createWebClient(String baseUrl) {
    return webClientBuilder.baseUrl(baseUrl).build();
  }

  public WebClient createWebClient(String baseUrl, String serverName) {
    return webClientBuilder.baseUrl(baseUrl).defaultHeader("server-name", serverName).build();
  }
}
