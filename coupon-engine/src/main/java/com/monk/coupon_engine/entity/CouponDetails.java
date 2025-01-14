package com.monk.coupon_engine.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import java.util.List;

@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponDetails {

  private Double threshold;
  private Double discount;

  @JsonProperty("product_id")
  private Long productId;

  @ElementCollection
  @JsonProperty("buy_products")
  private List<ProductDetails> buyProducts;

  @ElementCollection
  @JsonProperty("get_products")
  private List<ProductDetails> getProducts;

  @JsonProperty("repetition_limit")
  private Integer repetitionLimit;

  public Double getThreshold() {
    return threshold;
  }

  public void setThreshold(Double threshold) {
    this.threshold = threshold;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public List<ProductDetails> getBuyProducts() {
    return buyProducts;
  }

  public void setBuyProducts(List<ProductDetails> buyProducts) {
    this.buyProducts = buyProducts;
  }

  public List<ProductDetails> getGetProducts() {
    return getProducts;
  }

  public void setGetProducts(List<ProductDetails> getProducts) {
    this.getProducts = getProducts;
  }

  public Integer getRepetitionLimit() {
    return repetitionLimit;
  }

  public void setRepetitionLimit(Integer repetitionLimit) {
    this.repetitionLimit = repetitionLimit;
  }
}
