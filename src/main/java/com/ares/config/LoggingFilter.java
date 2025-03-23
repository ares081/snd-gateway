package com.ares.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.ares.helper.TraceHelper;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter {
  private final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

  private static final String TRACE_ID = "traceId";
  private static final String START_TIME = "startTime";

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String traceId = TraceHelper.generate();
    MDC.put(TRACE_ID, traceId);
    ServerHttpRequest request = exchange.getRequest();

    // Add trace ID to MDC and exchange attributes
    exchange.getAttributes().put(TRACE_ID, traceId);
    exchange.getAttributes().put(START_TIME, System.currentTimeMillis());

    // Create mutable request with trace ID header
    ServerHttpRequest mutatedRequest = request.mutate().header(TRACE_ID, traceId).build();

    // Log request details
    logger.info("Request - Remote: {}, Method: {}, Path: {}, Headers: {}, Query: {}",
        request.getRemoteAddress(), request.getMethod(), request.getPath(), request.getHeaders(),
        request.getQueryParams());

    return chain.filter(exchange.mutate().request(mutatedRequest).build())
        .transformDeferred(call -> Mono.fromCallable(() -> {
          return call;
        }).flatMap(c -> c).doFinally(signalType -> {
          Long startTime = exchange.getAttribute(START_TIME);
          if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            ServerHttpResponse response = exchange.getResponse();
            logger.info("Response - Duration: {}ms, Status: {}", duration,
                response.getStatusCode());
          }
          MDC.remove(TRACE_ID);
        }));
  }

}
