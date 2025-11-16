package com.pap_shop.service;

import com.pap_shop.dto.AnnouncementRequest;
import com.pap_shop.dto.AnnouncementResponse;
import com.pap_shop.entity.Announcement;
import com.pap_shop.entity.Product;
import com.pap_shop.exception.ResourceNotFoundException;
import com.pap_shop.repository.AnnouncementRepository;
import com.pap_shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing system announcements
 * Broadcasts announcements via WebSocket to all connected clients
 */
@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ProductRepository productRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Create and broadcast announcement
     * 
     * @param request Announcement details
     * @return Created announcement
     */
    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        Product product = null;
        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        }

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .product(product)
                .build();

        Announcement saved = announcementRepository.save(announcement);
        AnnouncementResponse response = mapToResponse(saved);

        // Broadcast to all connected clients
        messagingTemplate.convertAndSend("/topic/announcements", response);

        return response;
    }

    /**
     * Get all active announcements
     * 
     * @return List of active announcements
     */
    public List<AnnouncementResponse> getActiveAnnouncements() {
        return announcementRepository.findByIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all announcements (Admin)
     * 
     * @return List of all announcements
     */
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update announcement
     * 
     * @param id Announcement ID
     * @param request Updated details
     * @return Updated announcement
     */
    @Transactional
    public AnnouncementResponse updateAnnouncement(Integer id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));

        announcement.setTitle(request.getTitle());
        announcement.setMessage(request.getMessage());

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            announcement.setProduct(product);
        } else {
            announcement.setProduct(null);
        }

        Announcement updated = announcementRepository.save(announcement);
        AnnouncementResponse response = mapToResponse(updated);

        // Broadcast update
        messagingTemplate.convertAndSend("/topic/announcements/update", response);

        return response;
    }

    /**
     * Toggle announcement active status
     * 
     * @param id Announcement ID
     * @return Updated announcement
     */
    @Transactional
    public AnnouncementResponse toggleActive(Integer id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));

        announcement.setIsActive(!announcement.getIsActive());
        Announcement updated = announcementRepository.save(announcement);

        // Broadcast status change
        messagingTemplate.convertAndSend("/topic/announcements/status", mapToResponse(updated));

        return mapToResponse(updated);
    }

    /**
     * Delete announcement
     * 
     * @param id Announcement ID
     */
    @Transactional
    public void deleteAnnouncement(Integer id) {
        if (!announcementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Announcement not found");
        }

        announcementRepository.deleteById(id);

        // Broadcast deletion
        messagingTemplate.convertAndSend("/topic/announcements/delete", id);
    }

    private AnnouncementResponse mapToResponse(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getMessage(),
                announcement.getProduct() != null ? announcement.getProduct().getId() : null,
                announcement.getProduct() != null ? announcement.getProduct().getName() : null,
                announcement.getIsActive(),
                announcement.getCreatedAt()
        );
    }
}
