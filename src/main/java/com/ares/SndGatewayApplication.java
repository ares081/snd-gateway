package com.ares;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class SndGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(SndGatewayApplication.class, args);
  }

  @GetMapping("/health")
  public String hello() {
    return "Hello World!";
  }
}
