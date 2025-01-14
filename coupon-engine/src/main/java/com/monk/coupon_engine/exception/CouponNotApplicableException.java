package com.monk.coupon_engine.exception;

public class CouponNotApplicableException extends RuntimeException {
  public CouponNotApplicableException(String message) {
    super(message);
  }
}
