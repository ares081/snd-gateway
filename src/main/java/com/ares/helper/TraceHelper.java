package com.ares.helper;

import java.util.UUID;

public class TraceHelper {
  public static final String TRACE_ID = "traceId";

  public static String generate() {
    UUID uuid = UUID.randomUUID();
    long l = uuid.getMostSignificantBits() >>> 24; // 取高24位
    return String.valueOf(l);
  }
}
