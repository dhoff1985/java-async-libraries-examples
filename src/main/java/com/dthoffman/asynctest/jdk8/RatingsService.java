package com.dthoffman.asynctest.jdk8;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public interface RatingsService {
  CompletableFuture<BigDecimal> getRating(String identifier);
}
