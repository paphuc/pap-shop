package com.pap_shop.repository;

import com.pap_shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductId(Integer productId);
    Optional<Review> findByProductIdAndUserId(Integer productId, Integer userId);
}
