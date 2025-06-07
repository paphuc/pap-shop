package com.pap_shop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvalidatedToken {
    @Id
    private String jti;
    private Instant expiryTime;
}
