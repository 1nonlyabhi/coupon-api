package com.monk.coupon_engine.exception;

public class CouponNotFoundException extends RuntimeException {

  public CouponNotFoundException(String msg) {
    super(msg);
  }
}
