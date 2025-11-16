package com.pap_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Integer id;
    private Integer productId;
    private String username;
    private String userFullName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
