package com.dthoffman.asynctest.jdk8;

import com.dthoffman.asynctest.jdk8.domain.HandleAbleFailure;
import com.dthoffman.asynctest.jdk8.domain.Product;

import java.util.concurrent.CompletableFuture;

public class ProductService {

  private PriceService priceService;

  private ProductInformationService productInformationService;

  private RatingsService ratingsService;

  public ProductService(PriceService priceService, ProductInformationService productInformationService, RatingsService ratingsService) {
    this.priceService = priceService;
    this.productInformationService = productInformationService;
    this.ratingsService = ratingsService;
  }

  public CompletableFuture<Product> getProduct(String identifier) {
    Product result = new Product(identifier);
    return CompletableFuture.allOf(
      productInformationService.getProductName(identifier).exceptionally(this::exceptionallyNull).thenAccept(result::setName),
      productInformationService.getProductDescription(identifier).exceptionally(this::exceptionallyNull).thenAccept(result::setDescription),
      ratingsService.getRating(identifier).exceptionally(this::exceptionallyNull).thenAccept(result::setRating),
      priceService.getPrice(identifier).exceptionally(this::exceptionallyNull).thenAccept(result::setPrice)).thenApply((voidArg) -> result);
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
