package com.pap_shop.controller;

import com.pap_shop.dto.ChatRequest;
import com.pap_shop.dto.ChatResponse;
import com.pap_shop.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for AI chatbot functionality.
 * Provides endpoint for customers to ask questions about products.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "RAG Chatbot API")
public class ChatController {

    private final ChatService chatService;

    /**
     * Handles chat requests from users.
     * Uses semantic search to find relevant products and generates AI response.
     *
     * @param request the chat request containing user message and optional topK parameter
     * @return ResponseEntity with AI answer and list of related products
     */
    @PostMapping
    @Operation(summary = "Chat với AI về sản phẩm", description = "Tìm kiếm sản phẩm bằng semantic search và trả lời câu hỏi của khách hàng")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.chat(request.getMessage(), request.getTopK());
        return ResponseEntity.ok(response);
    }
}
