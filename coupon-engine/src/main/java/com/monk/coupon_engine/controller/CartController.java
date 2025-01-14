package com.monk.coupon_engine.controller;

import com.monk.coupon_engine.entity.ApplicableCoupon;
import com.monk.coupon_engine.entity.ApplicableCouponsWrapper;
import com.monk.coupon_engine.entity.Cart;
import com.monk.coupon_engine.entity.CartWrapper;
import com.monk.coupon_engine.entity.UpdatedCartWrapper;
import com.monk.coupon_engine.exception.CouponExpiredException;
import com.monk.coupon_engine.exception.CouponNotApplicableException;
import com.monk.coupon_engine.exception.CouponNotFoundException;
import com.monk.coupon_engine.service.CartService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CartController {

  private static final Logger logger = LoggerFactory.getLogger(CartService.class);

  @Autowired private CartService cartService;

  @PostMapping("/applicable-coupons")
  public ResponseEntity<ApplicableCouponsWrapper> getApplicableCoupons(
      @RequestBody CartWrapper cartWrapper) {
    List<ApplicableCoupon> applicableCoupons =
        cartService.getApplicableCoupons(cartWrapper.getCart());
    if (applicableCoupons.isEmpty()) {
      logger.info("No coupon is applicable on this cart.");
      throw new ResponseStatusException(HttpStatus.OK, "No coupon is applicable on this cart.");
    }
    return ResponseEntity.ok(new ApplicableCouponsWrapper(applicableCoupons));
  }

  @PostMapping("/apply-coupon/{couponId}")
  public ResponseEntity<UpdatedCartWrapper> applyCoupon(
      @PathVariable Long couponId, @RequestBody CartWrapper cartWrapper) {
    try {
      Cart updatedCart = cartService.applyCoupon(cartWrapper.getCart(), couponId);
      return ResponseEntity.ok(new UpdatedCartWrapper(updatedCart));
    } catch (CouponExpiredException | CouponNotApplicableException | CouponNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
