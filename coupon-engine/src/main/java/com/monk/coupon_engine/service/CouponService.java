package com.monk.coupon_engine.service;

import com.monk.coupon_engine.entity.Coupon;
import com.monk.coupon_engine.entity.ProductDetails;
import com.monk.coupon_engine.repository.CouponRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

  @Autowired private CouponRepository couponRepository;

  public Coupon createCoupon(Coupon coupon) {
    return couponRepository.save(coupon);
  }

  public List<Coupon> getAllCoupons() {
    return couponRepository.findAll();
  }

  public Optional<Coupon> getCouponById(Long id) {
    return couponRepository.findById(id);
  }

  public Optional<Coupon> updateCoupon(Long id, Coupon coupon) {
    return couponRepository
        .findById(id)
        .map(
            existingCoupon -> {
              existingCoupon.setType(coupon.getType());
              if (coupon.getDetails() != null) {
                existingCoupon.getDetails().setDiscount(coupon.getDetails().getDiscount());
                existingCoupon.getDetails().setThreshold(coupon.getDetails().getThreshold());
                existingCoupon.getDetails().setProductId(coupon.getDetails().getProductId());
                if (coupon.getType() == Coupon.CouponType.BXGY) {
                  existingCoupon
                      .getDetails()
                      .setBuyProducts(copyProducts(coupon.getDetails().getBuyProducts()));
                  existingCoupon
                      .getDetails()
                      .setGetProducts(copyProducts(coupon.getDetails().getGetProducts()));
                } else {
                  existingCoupon.getDetails().setBuyProducts(null);
                  existingCoupon.getDetails().setGetProducts(null);
                }
                existingCoupon
                    .getDetails()
                    .setRepetitionLimit(coupon.getDetails().getRepetitionLimit());
              }
              existingCoupon.setExpirationDate(coupon.getExpirationDate());
              return couponRepository.save(existingCoupon);
            });
  }

  public void deleteCoupon(Long id) {
    couponRepository.deleteById(id);
  }

  public List<ProductDetails> copyProducts(List<ProductDetails> products) {
    return products.stream().map(this::copyProductDetails).collect(Collectors.toList());
  }

  private ProductDetails copyProductDetails(ProductDetails productDetails) {
    ProductDetails copiedProductDetails = new ProductDetails();
    copiedProductDetails.setProductId(productDetails.getProductId());
    copiedProductDetails.setQuantity(productDetails.getQuantity());
    return copiedProductDetails;
  }
}
