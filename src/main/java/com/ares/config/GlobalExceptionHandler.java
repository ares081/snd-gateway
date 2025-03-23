package com.ares.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.ares.exception.BizErrorCode;
import com.ares.model.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
  private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
    logger.error("system error: {}", ex);
    // 1.获取响应对象
    ServerHttpResponse httpResponse = exchange.getResponse();
    // 2.设置响应状态码
    httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    // 3.设置响应头
    httpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    // 4.设置响应体
    Response<String> response = Response.failed(BizErrorCode.SYS_ERROR_CODE.getCode(),
        BizErrorCode.SYS_ERROR_CODE.getMsg());

    return httpResponse.writeWith(Mono.fromSupplier(() -> {
      DataBufferFactory bufferFactory = httpResponse.bufferFactory();
      try {
        return bufferFactory.wrap(objectMapper.writeValueAsBytes(response));
      } catch (JsonProcessingException e) {
        logger.error("json序列化异常", e);
        return bufferFactory.wrap(new byte[0]);
      }
    }));
  }

}
