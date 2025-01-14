package com.monk.coupon_engine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatedCartWrapper {

  @JsonProperty("updated_cart")
  private Cart updatedCart;

  public UpdatedCartWrapper(Cart updatedCart) {
    this.updatedCart = updatedCart;
  }
}
