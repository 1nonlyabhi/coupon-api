package com.monk.coupon_engine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
public class Coupon {

  public enum CouponType {
    @JsonProperty("cart-wise")
    CART_WISE,
    @JsonProperty("product-wise")
    PRODUCT_WISE,
    @JsonProperty("bxgy")
    BXGY
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private CouponType type;

  @Embedded private CouponDetails details;

  @JsonProperty("expiration_date")
  private LocalDate expirationDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CouponType getType() {
    return type;
  }

  public void setType(CouponType type) {
    this.type = type;
  }

  public CouponDetails getDetails() {
    return details;
  }

  public void setDetails(CouponDetails details) {
    this.details = details;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }
}
