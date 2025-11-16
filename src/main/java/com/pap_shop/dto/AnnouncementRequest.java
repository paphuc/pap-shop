package com.pap_shop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AnnouncementRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    private Integer productId;
}
