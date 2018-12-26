package com.dthoffman.asynctest.jdk8.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchClient {
  CompletableFuture<List<String>> keywordSearch(String keyword);
}
