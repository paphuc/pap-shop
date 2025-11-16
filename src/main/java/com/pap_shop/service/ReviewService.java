package com.pap_shop.service;

import com.pap_shop.dto.ReviewRequest;
import com.pap_shop.dto.ReviewResponse;
import com.pap_shop.entity.Product;
import com.pap_shop.entity.Review;
import com.pap_shop.entity.User;
import com.pap_shop.repository.ProductRepository;
import com.pap_shop.repository.ReviewRepository;
import com.pap_shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing product reviews
 * Handles review creation, updates, deletion, and retrieval
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Add a new review for a product
     * Validates that user hasn't already reviewed the product
     * 
     * @param productId Product ID to review
     * @param username User's username
     * @param request Review details including rating and comment
     * @return Created review response
     * @throws RuntimeException if user not found, product not found, or user already reviewed
     */
    @Transactional
    public ReviewResponse addReview(Integer productId, String username, ReviewRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (reviewRepository.findByProductIdAndUserId(productId, user.getId()).isPresent()) {
            throw new RuntimeException("You have already reviewed this product");
        }
        
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    /**
     * Update an existing review
     * Only the review owner can update their review
     * 
     * @param reviewId Review ID to update
     * @param username User's username
     * @param request Updated review details
     * @return Updated review response
     * @throws RuntimeException if review not found or unauthorized access
     */
    @Transactional
    public ReviewResponse updateReview(Integer reviewId, String username, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!review.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to update this review");
        }
        
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        Review updated = reviewRepository.save(review);
        return mapToResponse(updated);
    }

    /**
     * Delete a review
     * Only the review owner can delete their review
     * 
     * @param reviewId Review ID to delete
     * @param username User's username
     * @throws RuntimeException if review not found or unauthorized access
     */
    @Transactional
    public void deleteReview(Integer reviewId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!review.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to delete this review");
        }
        
        reviewRepository.delete(review);
    }

    /**
     * Get all reviews for a specific product
     * 
     * @param productId Product ID
     * @return List of product reviews
     */
    public List<ReviewResponse> getProductReviews(Integer productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map Review entity to ReviewResponse DTO
     * 
     * @param review Review entity
     * @return Review response DTO
     */
    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getUser().getUsername(),
                review.getUser().getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
