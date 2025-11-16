package com.pap_shop.controller;

import com.pap_shop.dto.AnnouncementRequest;
import com.pap_shop.dto.AnnouncementResponse;
import com.pap_shop.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing system announcements
 * Admin can create/update/delete, all users can view active announcements
 */
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcement", description = "System announcement management API")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * Create announcement (Admin only)
     * Broadcasts to all connected clients via WebSocket
     * 
     * @param request Announcement details
     * @return Created announcement
     */
    @PostMapping
    @Operation(summary = "Create announcement (Admin)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<AnnouncementResponse> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.createAnnouncement(request));
    }

    /**
     * Get all active announcements (Public)
     * 
     * @return List of active announcements
     */
    @GetMapping("/active")
    @Operation(summary = "Get active announcements")
    public ResponseEntity<List<AnnouncementResponse>> getActiveAnnouncements() {
        return ResponseEntity.ok(announcementService.getActiveAnnouncements());
    }

    /**
     * Get all announcements (Admin only)
     * 
     * @return List of all announcements
     */
    @GetMapping
    @Operation(summary = "Get all announcements (Admin)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    /**
     * Update announcement (Admin only)
     * 
     * @param id Announcement ID
     * @param request Updated details
     * @return Updated announcement
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update announcement (Admin)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @PathVariable Integer id,
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, request));
    }

    /**
     * Toggle announcement active status (Admin only)
     * 
     * @param id Announcement ID
     * @return Updated announcement
     */
    @PutMapping("/{id}/toggle")
    @Operation(summary = "Toggle announcement status (Admin)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<AnnouncementResponse> toggleActive(@PathVariable Integer id) {
        return ResponseEntity.ok(announcementService.toggleActive(id));
    }

    /**
     * Delete announcement (Admin only)
     * 
     * @param id Announcement ID
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete announcement (Admin)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Integer id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok("Announcement deleted successfully");
    }
}
