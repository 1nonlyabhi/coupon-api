package com.monk.coupon_engine.repository;

import com.monk.coupon_engine.entity.Coupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

  Optional<Coupon> findById(Long id);

  List<Coupon> findAll();

  List<Coupon> findByType(Coupon.CouponType type);

  List<Coupon> findByExpirationDateGreaterThanEqualOrExpirationDateIsNull(
      java.time.LocalDate currentDate);
}
