package com.monk.coupon_engine.exception;

public class CouponExpiredException extends RuntimeException {

  public CouponExpiredException(String message) {
    super(message);
  }
}
