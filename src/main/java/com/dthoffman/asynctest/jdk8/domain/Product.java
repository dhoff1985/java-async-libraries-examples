package com.dthoffman.asynctest.jdk8.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class Product {

  @Getter
  private String identifier;

  @Getter
  @Setter
  private String name;

  @Getter
  @Setter
  private String description;

  @Getter
  @Setter
  private BigDecimal rating;

  @Getter
  @Setter
  private BigDecimal price;

  public Product(String identifier) {
    this.identifier = identifier;
  }

}
