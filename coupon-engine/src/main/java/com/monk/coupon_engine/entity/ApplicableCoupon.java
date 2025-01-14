package com.monk.coupon_engine.entity;

public class ApplicableCoupon {

  private long couponId;
  private double discount;
  private Coupon.CouponType type;

  public ApplicableCoupon(long couponId, Coupon.CouponType type, double discount) {
    this.couponId = couponId;
    this.discount = discount;
    this.type = type;
  }

  public ApplicableCoupon() {}

  public long getCouponId() {
    return couponId;
  }

  public void setCouponId(long couponId) {
    this.couponId = couponId;
  }

  public double getDiscount() {
    return discount;
  }

  public void setDiscount(double discount) {
    this.discount = discount;
  }

  public Coupon.CouponType getType() {
    return type;
  }

  public void setType(Coupon.CouponType type) {
    this.type = type;
  }
}
