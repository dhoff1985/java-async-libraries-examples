package com.dthoffman.asynctest.jdk8.service

import com.dthoffman.asynctest.jdk8.client.PriceClient
import com.dthoffman.asynctest.jdk8.client.ProductInformationClient
import com.dthoffman.asynctest.jdk8.service.ProductService
import com.dthoffman.asynctest.jdk8.client.RatingsClient
import com.dthoffman.asynctest.jdk8.client.SearchClient
import com.dthoffman.asynctest.jdk8.domain.HandleAbleFailure
import com.dthoffman.asynctest.jdk8.domain.Product
import com.dthoffman.asynctest.jdk8.domain.UnhandleAbleFailure
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

import static java.math.BigDecimal.valueOf
import static java.util.concurrent.CompletableFuture.completedFuture
import static java.util.concurrent.CompletableFuture.failedFuture

class ProductServiceSpec extends Specification {


  ProductInformationClient mockInfoService = Mock(ProductInformationClient)
  RatingsClient mockRatingsService = Mock(RatingsClient)
  PriceClient mockPriceService = Mock(PriceClient)

  ProductService productService = new ProductService(mockPriceService, mockInfoService, mockRatingsService)


  def "product service returns products with name, description, price and rating"() {
    setup:
    String identifier = '123'

    when:
    Product product = productService.getProduct(identifier).get()

    then:
    product.identifier == identifier
    product.name == "Taco Machine"
    product.description == "Put money in, taco comes out"
    product.rating == 4.5
    product.price == 29.99

    1 * mockInfoService.getProductName(identifier) >> completedFuture("Taco Machine")
    1 * mockInfoService.getProductDescription(identifier) >> completedFuture("Put money in, taco comes out")
    1 * mockPriceService.getPrice(identifier) >> completedFuture(valueOf(29.99))
    1 * mockRatingsService.getRating(identifier) >> completedFuture(valueOf(4.5))
  }

  @Unroll
  def "product service returns null for values when encountering handleable exceptions"() {
    setup:
    String identifier = '123'

    def nameFuture = nameResponseFail ? handleAbleFailure() : completedFuture("Taco Machine")
    String expectedName = nameResponseFail ? null : "Taco Machine"

    def descriptionFuture = descriptionResponseFail ? handleAbleFailure() : completedFuture("is good")
    String expectedDescription = descriptionResponseFail ? null : "is good"

    def ratingFuture = ratingResponseFail ? handleAbleFailure() : completedFuture(valueOf(4.5))
    BigDecimal expectedRating = ratingResponseFail ? null : valueOf(4.5)

    def priceFuture = priceResponseFail ? handleAbleFailure() : completedFuture(valueOf(29.99))
    BigDecimal expectedPrice = priceResponseFail ? null : valueOf(29.99)

    when:
    Product product = productService.getProduct(identifier).get()

    then:
    product.identifier == identifier
    product.name == expectedName
    product.description == expectedDescription
    product.rating == expectedRating
    product.price == expectedPrice

    1 * mockInfoService.getProductName(identifier) >> nameFuture
    1 * mockInfoService.getProductDescription(identifier) >> descriptionFuture
    1 * mockRatingsService.getRating(identifier) >> ratingFuture
    1 * mockPriceService.getPrice(identifier) >> priceFuture

    where:
    nameResponseFail | descriptionResponseFail | priceResponseFail | ratingResponseFail
    true             | false                   | false             | false
    false            | true                    | false             | false
    false            | false                   | true              | false
    false            | false                   | false             | true
  }

  static CompletableFuture handleAbleFailure() {
    failedFuture(new HandleAbleFailure())
  }

  @Unroll
  def "product service fails for values when encountering handleable exceptions"() {
    setup:
    String identifier = '123'
    def nameFuture = nameResponseFail ? unhandleAbleFailure() : completedFuture("Taco Machine")
    def descriptionFuture = descriptionResponseFail ? unhandleAbleFailure() : completedFuture("is good")
    def ratingFuture = ratingResponseFail ? unhandleAbleFailure() : completedFuture(valueOf(4.5))
    def priceFuture = priceResponseFail ? unhandleAbleFailure() : completedFuture(valueOf(29.99))

    when:
    productService.getProduct(identifier).get()

    then:
    ExecutionException e = thrown(ExecutionException)
    e.cause instanceof UnhandleAbleFailure

    1 * mockInfoService.getProductName(identifier) >> nameFuture
    1 * mockInfoService.getProductDescription(identifier) >> descriptionFuture
    1 * mockRatingsService.getRating(identifier) >> ratingFuture
    1 * mockPriceService.getPrice(identifier) >> priceFuture

    where:
    nameResponseFail | descriptionResponseFail | priceResponseFail | ratingResponseFail
    true             | false                   | false             | false
    false            | true                    | false             | false
    false            | false                   | true              | false
    false            | false                   | false             | true
  }

  static CompletableFuture unhandleAbleFailure() {
    failedFuture(new UnhandleAbleFailure())
  }
}
