package com.monk.coupon_engine.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItem {

  @JsonProperty("product_id")
  private Long productId;

  private Integer quantity;
  private Double price;

  @JsonProperty("total_discount")
  private Double totalDiscount;

  public CartItem(Long productId, Integer quantity, Double price, Double totalDiscount) {
    this.productId = productId;
    this.quantity = quantity;
    this.price = price;
    this.totalDiscount = totalDiscount;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTotalDiscount() {
    return totalDiscount;
  }

  public void setTotalDiscount(Double totalDiscount) {
    this.totalDiscount = totalDiscount;
  }
}
