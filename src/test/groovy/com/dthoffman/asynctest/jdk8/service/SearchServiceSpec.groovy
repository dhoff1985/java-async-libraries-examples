package com.dthoffman.asynctest.jdk8.service

import com.dthoffman.asynctest.jdk8.client.SearchClient
import com.dthoffman.asynctest.jdk8.domain.Product
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class SearchServiceSpec extends Specification {

  ProductService mockProductService = Mock(ProductService)

  SearchClient mockSearchClient = Mock(SearchClient)

  SearchService searchService = new SearchService(mockProductService, mockSearchClient)

  def "returns products based on search result"() {
    setup:
    Product mockProduct1 = Mock(Product)
    Product mockProduct2 = Mock(Product)
    Product mockProduct3 = Mock(Product)

    when:
    List<Product> results = searchService.searchProducts("tacos").get()

    then:
    results == [mockProduct1, mockProduct2, mockProduct3]
    1 * mockSearchClient.keywordSearch("tacos") >> CompletableFuture.completedFuture(["product-1", "product-2", "product-3"])
    1 * mockProductService.getProduct("product-1") >> CompletableFuture.completedFuture(mockProduct1)
    1 * mockProductService.getProduct("product-2") >> CompletableFuture.completedFuture(mockProduct2)
    1 * mockProductService.getProduct("product-3") >> CompletableFuture.completedFuture(mockProduct3)
  }

}
