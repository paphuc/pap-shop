package com.pap_shop.repository;

import com.pap_shop.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, Integer> {
    boolean existsByJti(String jti);
}
