package com.ares.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserInfo {
  private Long userId;
  private String username;
  private String phone;
  private String phonePlain;
}
