package com.ares.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response<T> {
  public static final int OK_CODE = 2000;

  public static final boolean OK_FLAG = true;
  public static final boolean FAILED_FLAG = false;

  private boolean flag;
  private int code;
  private String msg;
  private T data;

  public Response() {}

  public Response(boolean flag, int code) {
    this(flag, code, "");
  }

  public Response(boolean flag, int code, T data) {
    this(flag, code, "", data);
  }

  public Response(boolean flag, int code, String msg) {
    this(flag, code, msg, null);
  }

  public Response(boolean flag, int code, String msg, T data) {
    this.flag = flag;
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public static <T> Response<T> ok(T data) {
    return new Response<>(OK_FLAG, OK_CODE, data);
  }

  public static <T> Response<T> failed(int code) {
    return new Response<>(FAILED_FLAG, code, "");
  }

  public static <T> Response<T> failed(int code, String msg) {
    return new Response<>(FAILED_FLAG, code, msg);
  }

  public static <T> Response<T> failed(int code, String msg, T data) {
    return new Response<>(FAILED_FLAG, code, msg, data);
  }
}

