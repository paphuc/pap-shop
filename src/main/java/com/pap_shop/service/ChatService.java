package com.pap_shop.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.pap_shop.dto.ChatResponse;
import com.pap_shop.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling RAG-based chatbot conversations.
 * Combines semantic product search with Gemini AI to answer customer queries.
 */
@Service
public class ChatService {

    private final ProductSearchService searchService;
    private final Client client;

    /**
     * Constructs ChatService with product search and Gemini client.
     *
     * @param searchService service for semantic product search
     * @param apiKey the Google Gemini API key from configuration
     */
    public ChatService(ProductSearchService searchService, @Value("${gemini.api.key}") String apiKey) {
        this.searchService = searchService;
        this.client = new Client.Builder().apiKey(apiKey).build();
    }

    /**
     * Processes user message and generates AI response with relevant products.
     * Uses RAG (Retrieval-Augmented Generation) approach:
     * 1. Searches for relevant products using semantic search
     * 2. Builds context with product information
     * 3. Generates natural language response using Gemini
     *
     * @param userMessage the user's question or message
     * @param topK maximum number of relevant products to retrieve
     * @return ChatResponse containing AI answer and related products
     */
    public ChatResponse chat(String userMessage, int topK) {
        List<Product> relatedProducts = searchService.semanticSearch(userMessage, topK);
        
        StringBuilder context = new StringBuilder("Dựa vào các sản phẩm sau:\n\n");
        for (Product p : relatedProducts) {
            context.append(String.format("- %s: %s (Giá: %s VNĐ, Còn: %d)\n", 
                p.getName(), 
                p.getDescription() != null ? p.getDescription() : "Không có mô tả",
                p.getPrice(),
                p.getStock()
            ));
        }
        
        context.append("\nCâu hỏi của khách: ").append(userMessage);
        context.append("\n\nHãy trả lời ngắn gọn, chuyên nghiệp và gợi ý sản phẩm phù hợp.");
        
        GenerateContentResponse response = client.models.generateContent(
            "gemini-2.0-flash-lite",
            context.toString(),
            null
        );
        
        String answer = response.text();
        
        return new ChatResponse(answer, relatedProducts);
    }
}
