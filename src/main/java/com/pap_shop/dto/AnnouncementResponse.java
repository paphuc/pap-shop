package com.pap_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnnouncementResponse {
    private Integer id;
    private String title;
    private String message;
    private Integer productId;
    private String productName;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
