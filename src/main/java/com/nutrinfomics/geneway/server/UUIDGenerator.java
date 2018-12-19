package com.nutrinfomics.geneway.server;

import java.util.UUID;

import javax.inject.Singleton;

@Singleton
public class UUIDGenerator {
  public UUID randomUUID() {
    return UUID.randomUUID();
  }
}
