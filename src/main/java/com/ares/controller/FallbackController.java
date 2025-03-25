package com.ares.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ares.model.Response;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class FallbackController {

  @GetMapping("/fallback")
  public Mono<Response<Object>> fallback() {
    return Mono.just(new Response<>(Response.FAILED_FLAG, Response.FAILED_CODE, "服务暂时不可用"));
  }
}
