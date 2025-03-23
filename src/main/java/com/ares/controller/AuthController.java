package com.ares.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ares.helper.TokenHelper;
import com.ares.model.LoginRequest;
import com.ares.model.Response;
import com.ares.service.AuthService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private AuthService authService;

  @PostMapping("/login")
  public Mono<ResponseEntity<Response<?>>> login(ServerHttpRequest httpRequest,
      @RequestBody LoginRequest request) {
    logger.info("login request: {}", request);
    return authService.login(httpRequest, request)
        .map(res -> ResponseEntity.ok()
            .header(TokenHelper.AUTH_TOKEN_HEADER, TokenHelper.AUTH_TOKEN_PREFIX + res.getToken())
            .body(Response.ok(res)));
  }
}
