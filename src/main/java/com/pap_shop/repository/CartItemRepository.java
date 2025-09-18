package com.pap_shop.repository;

import com.pap_shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
    
    @Modifying
    @Query(value = "DELETE FROM cart_items WHERE cart_id = ?1", nativeQuery = true)
    void deleteByCartId(Integer cartId);
    
    @Modifying
    @Query(value = "ALTER TABLE cart_items AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}