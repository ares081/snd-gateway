package com.ares.service;

import com.ares.model.Response;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import com.ares.config.webclient.CustomWebClientFactory;
import com.ares.helper.TokenHelper;
import com.ares.helper.TraceHelper;
import com.ares.model.LoginRequest;
import com.ares.model.LoginResponse;
import com.ares.model.UserInfo;
import com.google.gson.Gson;
import reactor.core.publisher.Mono;

@Service
public class AuthService {
  private final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private static final Duration TOKEN_VALIDITY = Duration.ofHours(24);

  @Autowired
  private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

  @Autowired
  private Gson gson;

  @Autowired
  private TokenHelper tokenHelper;

  private final WebClient webClient;

  public AuthService(CustomWebClientFactory webClientFactory) {
    this.webClient = webClientFactory.createWebClient();
  }

  /**
   * 登录
   *
   * @param httpRequest http请求
   * @param request 登录请求
   * @return 登录响应
   */

  public Mono<LoginResponse> login(ServerHttpRequest httpRequest, LoginRequest request) {
    logger.info("login request: {}", request);
    // 判断是否已登录
    String actualToken = tokenHelper.getActualToken(httpRequest);
    if (StringUtils.hasText(actualToken)) {
      return reactiveRedisTemplate.opsForValue().get(actualToken).filter(StringUtils::hasText)
          .map(userInfo -> {
            UserInfo user = gson.fromJson(userInfo, UserInfo.class);
            LoginResponse response = new LoginResponse();
            response.setUser(user);
            response.setToken(tokenHelper.encryptToken(actualToken));
            return response;
          }).switchIfEmpty(performLogin(request));
    }
    return performLogin(request);
  }

  private Mono<LoginResponse> performLogin(LoginRequest request) {
    return webClient.post().uri("/api/v1/user/login")
        .header(TraceHelper.TRACE_ID, TraceHelper.generate())
        .bodyValue(request)
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<Response<UserInfo>>() {}).flatMap(res -> {
          String token = TokenHelper.generateToken();
          String encryptedToken = tokenHelper.encryptToken(token);
          return reactiveRedisTemplate.opsForValue()
              .set(token, gson.toJson(res.getData()), TOKEN_VALIDITY)
              .thenReturn(getLoginResponse(encryptedToken, res.getData()));
        });
  }

  private LoginResponse getLoginResponse(String token, UserInfo userInfo) {
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setUser(userInfo);
    return response;
  }

}
