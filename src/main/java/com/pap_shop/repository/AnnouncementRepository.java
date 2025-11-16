package com.pap_shop.repository;

import com.pap_shop.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findByIsActiveTrueOrderByCreatedAtDesc();
}
