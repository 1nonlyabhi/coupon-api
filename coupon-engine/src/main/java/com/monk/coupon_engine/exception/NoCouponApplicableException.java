package com.monk.coupon_engine.exception;

public class NoCouponApplicableException extends RuntimeException {
  public NoCouponApplicableException(String message) {
    super(message);
  }
}
