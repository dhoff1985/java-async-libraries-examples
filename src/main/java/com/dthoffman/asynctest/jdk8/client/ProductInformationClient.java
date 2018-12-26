package com.dthoffman.asynctest.jdk8.client;

import java.util.concurrent.CompletableFuture;

public interface ProductInformationClient {
  CompletableFuture<String> getProductName(String identifier);

  CompletableFuture<String> getProductDescription(String identifier);
}
