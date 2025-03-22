package com.ares.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class NacosRouteDefinitionRepository implements RouteDefinitionRepository {
  private final Logger logger = LoggerFactory.getLogger(NacosRouteDefinitionRepository.class);
  private static final String SERVER_ADDR = "localhost:8848";
  private static final String DATA_ID = "snd-gateway";
  private static final String GROUP_ID = "DEFAULT_GROUP";

  @Autowired
  private Gson gson;

  @Autowired
  private ApplicationEventPublisher publisher;

  private final ConfigService configService;

  public NacosRouteDefinitionRepository() {
    try {
      configService = NacosFactory.createConfigService(SERVER_ADDR);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    addListener();
  }

  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    try {
      String content = configService.getConfig(DATA_ID, GROUP_ID, 5000);
      List<RouteDefinition> routeDefinitions = getListByStr(content);
      return Flux.fromIterable(routeDefinitions);
    } catch (Exception e) {
      logger.error("get route definitions error:{}", e);
    }
    return Flux.fromIterable(new ArrayList<>());
  }

  private void addListener() {
    try {
      ConfigService configService = NacosFactory.createConfigService(SERVER_ADDR);
      configService.addListener(DATA_ID, GROUP_ID, new Listener() {
        @Override
        public Executor getExecutor() {
          return null;
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
          publisher.publishEvent(new RefreshRoutesEvent(this));
        }
      });
    } catch (Exception e) {
      logger.error("add listener failed: {}", e.getCause());
    }
  }


  @Override
  public Mono<Void> save(Mono<RouteDefinition> route) {
    return null;
  }

  @Override
  public Mono<Void> delete(Mono<String> routeId) {
    return null;
  }

  private List<RouteDefinition> getListByStr(String content) {
    if (StringUtils.hasText(content)) {
      return gson.fromJson(content, new TypeToken<List<RouteDefinition>>() {}.getType());
    }
    return new ArrayList<>(0);
  }
}
