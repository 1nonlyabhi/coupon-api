package com.monk.coupon_engine.service;

import com.monk.coupon_engine.entity.ApplicableCoupon;
import com.monk.coupon_engine.entity.Cart;
import com.monk.coupon_engine.entity.CartItem;
import com.monk.coupon_engine.entity.Coupon;
import com.monk.coupon_engine.entity.ProductDetails;
import com.monk.coupon_engine.exception.CouponExpiredException;
import com.monk.coupon_engine.exception.CouponNotApplicableException;
import com.monk.coupon_engine.exception.CouponNotFoundException;
import com.monk.coupon_engine.repository.CouponRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

  private static final Logger logger = LoggerFactory.getLogger(CartService.class);

  @Autowired private CouponRepository couponRepository;

  public List<ApplicableCoupon> getApplicableCoupons(Cart cart) {
    List<ApplicableCoupon> applicableCoupons = new ArrayList<>();

    List<Coupon> coupons =
        couponRepository.findByExpirationDateGreaterThanEqualOrExpirationDateIsNull(
            LocalDate.now());

    if (coupons.isEmpty()) {
      logger.info("All coupons are expired.");
      return Collections.emptyList();
    }

    for (Coupon coupon : coupons) {
      ApplicableCoupon applicableCoupon = checkCoupon(cart, coupon, false);
      if (applicableCoupon != null) {
        applicableCoupons.add(applicableCoupon);
      }
    }

    return applicableCoupons;
  }

  public Cart applyCoupon(Cart cart, Long couponId) {
    Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
    if (optionalCoupon.isEmpty()) {
      throw new CouponNotFoundException("Coupon not found with ID: " + couponId);
    }

    Coupon coupon = optionalCoupon.get();
    if (isCouponExpired(coupon)) {
      throw new CouponExpiredException("Coupon has expired.");
    }

    return applyCoupon(cart, coupon);
  }

  public Cart applyCoupon(Cart cart, Coupon coupon) {
    ApplicableCoupon applicableCoupon = checkCoupon(cart, coupon, true);
    if (applicableCoupon != null) {
      updateCartSummary(cart, applicableCoupon);
    } else {
      logger.info("Coupon ID: {} Not Applicable on this cart.", coupon.getId());
      throw new CouponNotApplicableException("Coupon Not Applicable on this cart.");
    }
    return cart;
  }

  private ApplicableCoupon checkCoupon(Cart cart, Coupon coupon, boolean flag) {
    return switch (coupon.getType()) {
      case CART_WISE -> checkCartWiseCoupon(cart, coupon);
      case PRODUCT_WISE -> checkProductWiseCoupon(cart, coupon);
      case BXGY -> checkOrApplyBxGyCoupon(cart, coupon, flag);
      default -> null;
    };
  }

  private ApplicableCoupon checkCartWiseCoupon(Cart cart, Coupon coupon) {
    double cartTotal = calculateCartTotal(cart);
    if (cartTotal >= coupon.getDetails().getThreshold()) {
      double discount = cartTotal * coupon.getDetails().getDiscount() / 100;
      return new ApplicableCoupon(coupon.getId(), coupon.getType(), discount);
    }
    return null;
  }

  private ApplicableCoupon checkProductWiseCoupon(Cart cart, Coupon coupon) {
    for (CartItem item : cart.getItems()) {
      if (item.getProductId().equals(coupon.getDetails().getProductId())) {
        double discount =
            (item.getPrice() * item.getQuantity()) * coupon.getDetails().getDiscount() / 100;
        return new ApplicableCoupon(coupon.getId(), coupon.getType(), discount);
      }
    }
    return null;
  }

  private ApplicableCoupon checkOrApplyBxGyCoupon(Cart cart, Coupon coupon, boolean flag) {
    // Getting the coupon details
    List<ProductDetails> buyProducts = coupon.getDetails().getBuyProducts();
    List<ProductDetails> getProducts = coupon.getDetails().getGetProducts();
    int repetitionLimit = coupon.getDetails().getRepetitionLimit();

    // Check if cart contains enough of the buy products
    List<CartItem> buyCartItems = getProductsFromList(buyProducts, cart);
    List<CartItem> getCartItems = getProductsFromList(getProducts, cart);

    // If there are no buy or get products in the cart, return null
    if (buyCartItems.isEmpty() || getCartItems.isEmpty()) {
      return null;
    }

    // Calculate how many repetitions can be applied based on the available buy products
    int maxRepetitions =
        calculateRepetitionLimit(buyCartItems, coupon.getDetails().getBuyProducts());

    int repetitions = Math.min(maxRepetitions, repetitionLimit);
    if (repetitions <= 0) {
      return null; // No valid repetitions found, return null
    }

    // Calculate the discount value for the get products
    double totalDiscount = 0;
    for (ProductDetails details : getProducts) {
      int requiredQuantity = details.getQuantity() * repetitions;

      CartItem existingItem =
          getCartItems.stream()
              .filter(item -> item.getProductId().equals(details.getProductId()))
              .findFirst()
              .orElse(null);

      double price = getPriceForProduct(details.getProductId(), cart);
      totalDiscount += price * requiredQuantity;
      if (flag && existingItem != null) {
        // Change cart's existing GET products and discount
        handleExistingGetProduct(existingItem, requiredQuantity, price);
      }
    }

    // Return the applicable coupon with the calculated discount value
    if (totalDiscount > 0) {
      return new ApplicableCoupon(coupon.getId(), coupon.getType(), totalDiscount);
    }

    return null; // Return null if no discount is applicable
  }

  private double calculateCartTotal(Cart cart) {
    double total = 0;
    for (CartItem item : cart.getItems()) {
      total += item.getPrice() * item.getQuantity();
    }
    return total;
  }

  private List<CartItem> getProductsFromList(List<ProductDetails> productDetails, Cart cart) {
    List<CartItem> cartItems = new ArrayList<>();

    for (ProductDetails productDetail : productDetails) {
      CartItem cartItem =
          cart.getItems().stream()
              .filter(item -> item.getProductId().equals(productDetail.getProductId()))
              .findFirst()
              .orElse(null);

      if (cartItem != null) {
        cartItems.add(cartItem);
      }
    }

    return cartItems;
  }

  private boolean isCouponExpired(Coupon coupon) {
    return coupon.getExpirationDate() != null
        && coupon.getExpirationDate().isBefore(LocalDate.now());
  }

  private void handleExistingGetProduct(CartItem existingItem, int requiredQuantity, double price) {
    // Update the existing item's quantity
    int updatedQuantity = existingItem.getQuantity() + requiredQuantity;
    existingItem.setQuantity(updatedQuantity);

    // Apply the discount on the newly added quantity
    double additionalDiscount = price * requiredQuantity;
    double existingDiscount =
        existingItem.getTotalDiscount() != null ? existingItem.getTotalDiscount() : 0.0;
    existingItem.setTotalDiscount(existingDiscount + additionalDiscount);
  }

  private double getPriceForProduct(Long productId, Cart cart) {
    // Attempt to find the price from the cart items
    return cart.getItems().stream()
        .filter(item -> item.getProductId().equals(productId))
        .map(CartItem::getPrice)
        .findFirst()
        .orElse(0.0);
  }

  private int calculateRepetitionLimit(
      List<CartItem> buyCartItems, List<ProductDetails> buyProductDetails) {
    int maxRepetitions = Integer.MAX_VALUE;

    for (ProductDetails productDetails : buyProductDetails) {
      CartItem cartItem =
          buyCartItems.stream()
              .filter(item -> item.getProductId().equals(productDetails.getProductId()))
              .findFirst()
              .orElse(null);

      if (cartItem == null) {
        return 0; // Missing a required buy product
      }

      int productRepetitions = cartItem.getQuantity() / productDetails.getQuantity();
      maxRepetitions = Math.min(maxRepetitions, productRepetitions);
    }

    return maxRepetitions;
  }

  private void updateCartSummary(Cart cart, ApplicableCoupon applicableCoupon) {
    double totalPrice = calculateCartTotal(cart);

    cart.setTotalPrice(totalPrice);
    cart.setTotalDiscount(applicableCoupon.getDiscount());
    cart.setFinalPrice(totalPrice - applicableCoupon.getDiscount());
  }
}
