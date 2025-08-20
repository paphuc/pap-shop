package com.pap_shop.repository;

import com.pap_shop.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryTime < :now")
    void deleteExpiredTokens(@Param("now") Instant now);
}