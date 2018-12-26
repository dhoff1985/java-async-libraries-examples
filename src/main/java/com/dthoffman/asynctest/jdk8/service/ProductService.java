package com.dthoffman.asynctest.jdk8.service;

import com.dthoffman.asynctest.jdk8.client.PriceClient;
import com.dthoffman.asynctest.jdk8.client.ProductInformationClient;
import com.dthoffman.asynctest.jdk8.client.RatingsClient;
import com.dthoffman.asynctest.jdk8.domain.HandleAbleFailure;
import com.dthoffman.asynctest.jdk8.domain.Product;

import java.util.concurrent.CompletableFuture;

public class ProductService {

  private PriceClient priceClient;

  private ProductInformationClient productInformationClient;

  private RatingsClient ratingsService;

  public ProductService(PriceClient priceClient, ProductInformationClient productInformationClient, RatingsClient ratingsService) {
    this.priceClient = priceClient;
    this.productInformationClient = productInformationClient;
    this.ratingsService = ratingsService;
  }

  public CompletableFuture<Product> getProduct(String identifier) {
    Product result = new Product(identifier);
    return CompletableFuture.allOf(
      productInformationClient.getProductName(identifier).exceptionally(this::exceptionallyNull).thenAccept(result::setName),
      productInformationClient.getProductDescription(identifier).exceptionally(this::exceptionallyNull).thenAccept(result::setDescription),
      ratingsService.getRating(identifier).exceptionally(this::exceptionallyNull).thenAccept(result::setRating),
      priceClient.getPrice(identifier).exceptionally(this::exceptionallyNull).thenAccept(result::setPrice)).thenApply((voidArg) -> result);
  }

  private <T> T exceptionallyNull(Throwable error) {
    if (error instanceof HandleAbleFailure) {
      return null;
    } else if (error instanceof RuntimeException) {
      throw (RuntimeException) error;
    } else {
      throw new RuntimeException(error);
    }
  }

}
