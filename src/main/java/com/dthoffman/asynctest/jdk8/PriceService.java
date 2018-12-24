package com.dthoffman.asynctest.jdk8;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface PriceService {
  CompletableFuture<BigDecimal> getPrice(String price);
}
