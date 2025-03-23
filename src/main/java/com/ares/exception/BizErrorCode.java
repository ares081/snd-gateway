package com.ares.exception;

import lombok.Getter;

@Getter
public class BizErrorCode {
  private int code;
  private String msg;

  BizErrorCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static final BizErrorCode SYS_ERROR_CODE = new BizErrorCode(500, "系统内部错误");
}
