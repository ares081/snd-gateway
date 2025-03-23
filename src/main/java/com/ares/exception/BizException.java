package com.ares.exception;

public class BizException extends RuntimeException {

  private final int code;
  private final String msg;

  public BizException(int code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

}
