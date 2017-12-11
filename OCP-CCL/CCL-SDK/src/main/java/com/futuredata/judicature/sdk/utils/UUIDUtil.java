package com.futuredata.judicature.sdk.utils;

import java.util.UUID;

public class UUIDUtil {
  public static final String generatorID() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
