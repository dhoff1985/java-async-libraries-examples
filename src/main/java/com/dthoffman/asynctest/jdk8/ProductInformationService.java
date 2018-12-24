package com.dthoffman.asynctest.jdk8;

import java.util.concurrent.CompletableFuture;

public interface ProductInformationService {
  CompletableFuture<String> getProductName(String identifier);

  CompletableFuture<String> getProductDescription(String identifier);
}
