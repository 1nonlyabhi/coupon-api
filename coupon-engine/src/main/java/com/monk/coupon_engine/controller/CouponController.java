package com.monk.coupon_engine.controller;

import com.monk.coupon_engine.entity.Coupon;
import com.monk.coupon_engine.service.CouponService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
public class CouponController {

  @Autowired private CouponService couponService;

  @PostMapping
  public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
    return ResponseEntity.ok(couponService.createCoupon(coupon));
  }

  @GetMapping
  public ResponseEntity<List<Coupon>> getAllCoupons() {
    return ResponseEntity.ok(couponService.getAllCoupons());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
    return couponService
        .getCouponById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
    return couponService
        .updateCoupon(id, coupon)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
    couponService.deleteCoupon(id);
    return ResponseEntity.noContent().build();
  }
}
