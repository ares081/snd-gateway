package com.ares.config.webclient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@ConfigurationProperties(prefix = "snd.webclient")
public class CustomWebClientProperties {
  /**
   * 服务地址，不填写则为 http://serviceName
   */
  private String url;
  /**
   * 微服务名称，不填写就是 configs 这个 map 的 key
   */
  private String serverName;
  /**
   * 连接超时时间
   */
  private int connectTimeout = 300;

  /**
   * 响应超时时间
   */
  private int readTimeout = 3000;

  /**
   * 是否启用日志
   */
  private boolean enableLogging = false;

}
