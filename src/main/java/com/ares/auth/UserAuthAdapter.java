package com.ares.auth;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.alibaba.nacos.common.utils.StringUtils;
import com.ares.helper.TokenHelper;
import reactor.core.publisher.Mono;

@Component
public class UserAuthAdapter implements GlobalFilter{

  // 往后端传递
  private static final String USER_HEADER = "user";


  private static final List<String> WHITE_LIST = Arrays.asList("/auth/login", "/auth/register");

  @Autowired
  private TokenHelper tokenHelper;

  @Autowired
  private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getURI().getPath();

    // 验证是否在白名单中
    if (isWhiteListPath(path)) {
      return chain.filter(exchange);
    }
    String actualToken = tokenHelper.getActualToken(request);
    if (!StringUtils.hasText(actualToken)) {
      return unauthorizedResponse(exchange);
    }
    // 获取reids中的 token
    String userInfo = reactiveRedisTemplate.opsForValue().get(actualToken).block();
    if (!StringUtils.hasText(userInfo)) {
      return unauthorizedResponse(exchange);
    }
    exchange.getAttributes().put(USER_HEADER, userInfo);
    // 验证通过继续处理请求
    return chain.filter(exchange);
  }

  private boolean isWhiteListPath(String path) {
    return WHITE_LIST.stream()
        .anyMatch(pattern -> path.startsWith(pattern) || path.matches(pattern.replace("**", ".*")));
  }

  private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    return response.setComplete();
  }
}
