package com.dthoffman.asynctest.jdk8.service;

import com.dthoffman.asynctest.jdk8.client.SearchClient;
import com.dthoffman.asynctest.jdk8.domain.Product;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SearchService {

  private ProductService productService;

  private SearchClient searchClient;


  public SearchService(ProductService productService, SearchClient searchClient) {
    this.productService = productService;
    this.searchClient = searchClient;
  }

  public CompletableFuture<List<Product>> searchProducts(String keyword) {
    return searchClient.keywordSearch(keyword).thenCompose((List<String> productIds) -> {
      List<CompletableFuture<Product>> productFutures = productIds.stream().map(productService::getProduct).collect(Collectors.toList());
      return CompletableFuture.allOf(productFutures.toArray(new CompletableFuture[0])).thenApply( (voidArg) ->  productFutures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    });
  }
}
