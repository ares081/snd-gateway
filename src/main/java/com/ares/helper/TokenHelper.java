package com.ares.helper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import com.alibaba.nacos.common.utils.StringUtils;

@Configuration
public class TokenHelper {
  private final Logger logger = LoggerFactory.getLogger(TokenHelper.class);

  @Value("${auth.token.secret-key}")
  private String secretKey;

  public static final String AUTH_TOKEN_PREFIX = "snd";
  public static final String AUTH_TOKEN_HEADER = "X-Auth-Token";

  private static final String ALGORITHM = "AES";

  public String encryptToken(String token) {
    if (!StringUtils.hasText(token)) {
      return null;
    }
    try {
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
      byte[] encryptedBytes = cipher.doFinal(token.getBytes());
      return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);
    } catch (Exception e) {
      logger.error("encryptToken error: {}", e);
    }
    return null;
  }

  public String decryptToken(String encryptedToken) {
    if (!StringUtils.hasText(encryptedToken)) {
      return null;
    }
    try {
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
      byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedToken));
      return new String(decryptedBytes);
    } catch (Exception e) {
      logger.info("decryptToken failed: {}", e.getMessage());
    }
    return null;
  }

  public String getActualToken(ServerHttpRequest request) {
    // 获取token
    String token = request.getHeaders().getFirst(AUTH_TOKEN_HEADER);
    if (!StringUtils.hasText(token) || !token.startsWith(AUTH_TOKEN_PREFIX)) {
      return null;
    }
    String encryptedToken = token.substring(AUTH_TOKEN_PREFIX.length());
    return decryptToken(encryptedToken);
  }

  public static String generateToken() {
    return UUID.randomUUID().toString().replace("-", "");
  }
}
