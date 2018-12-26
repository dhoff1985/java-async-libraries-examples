package com.dthoffman.asynctest.jdk8.client;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface PriceClient {
  CompletableFuture<BigDecimal> getPrice(String price);
}
