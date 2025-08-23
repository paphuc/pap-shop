package com.pap_shop.repository;

import com.pap_shop.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.Instant;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Integer> {
    boolean existsByJti(String jti);
    @Modifying
    @Transactional
    @Query("DELETE FROM InvalidatedToken t WHERE t.expiryTime < :now")
    void deleteByExpiryTimeBefore(@Param("now") Instant now);

}
