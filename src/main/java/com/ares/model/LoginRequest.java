package com.ares.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
  // 登陆方式：1-验证码，2-密码
  private Integer type;
  private String phone;
  private String password;
  // 验证码
  private String code;

}
