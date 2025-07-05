package com.pap_shop.repository;

import com.pap_shop.entity.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM StockEntry s WHERE s.product.id = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}
