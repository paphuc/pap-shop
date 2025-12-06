package com.pap_shop.dto;

import com.pap_shop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Data transfer object for chat responses.
 * Contains the AI-generated answer and list of relevant products.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    /**
     * The AI-generated answer to the user's question.
     */
    private String answer;
    
    /**
     * List of products relevant to the user's query.
     * Sorted by semantic similarity score.
     */
    private List<Product> relatedProducts;
}
