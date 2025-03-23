package com.ares.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
  private String token;
  private UserInfo user;
}
