package com.pap_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for chat requests.
 * Contains the user's message and optional parameters for search.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {
    /**
     * The user's message or question.
     */
    private String message;
    
    /**
     * Maximum number of relevant products to retrieve.
     * Default is 5.
     */
    private Integer topK = 5;
}
