package com.monk.coupon_engine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ApplicableCouponsWrapper {

  @JsonProperty("applicable_coupons")
  private List<ApplicableCoupon> applicableCoupons;

  public ApplicableCouponsWrapper(List<ApplicableCoupon> applicableCoupons) {
    this.applicableCoupons = applicableCoupons;
  }
}
