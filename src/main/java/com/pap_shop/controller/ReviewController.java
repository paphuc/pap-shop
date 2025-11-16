package com.pap_shop.controller;

import com.pap_shop.dto.ReviewRequest;
import com.pap_shop.dto.ReviewResponse;
import com.pap_shop.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing product reviews
 * Handles review creation, updates, deletion, and retrieval
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Product review management API")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Add a new review for a product
     * @param productId Product ID to review
     * @param request Review details including rating and comment
     * @param authentication User authentication info
     * @return Created review
     */
    @PostMapping("/products/{productId}")
    @Operation(summary = "Add review for product", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable Integer productId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(reviewService.addReview(productId, username, request));
    }

    /**
     * Update an existing review
     * @param reviewId Review ID to update
     * @param request Updated review details
     * @param authentication User authentication info
     * @return Updated review
     */
    @PutMapping("/{reviewId}")
    @Operation(summary = "Update review", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Integer reviewId,
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(reviewService.updateReview(reviewId, username, request));
    }

    /**
     * Delete a review
     * @param reviewId Review ID to delete
     * @param authentication User authentication info
     * @return No content
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete review", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer reviewId,
            Authentication authentication) {
        String username = authentication.getName();
        reviewService.deleteReview(reviewId, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all reviews for a specific product
     * @param productId Product ID
     * @return List of product reviews
     */
    @GetMapping("/products/{productId}")
    @Operation(summary = "Get product reviews")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Integer productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }
}
