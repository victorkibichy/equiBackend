package com.EquiFarm.EquiFarm.Tracking;

import java.time.LocalDateTime;

import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;

import jakarta.persistence.PrePersist;

public class TrackingEntityListener {
    @PrePersist
    public void onPrePersist(Object entity) {
        if (entity instanceof TrackingEntity) {
            TrackingEntity trackingEntity = (TrackingEntity) entity;
            User currentUser = SecurityUtils.getCurrentUser();

            trackingEntity.setCreatedAt(LocalDateTime.now());
            if (currentUser != null) {
                trackingEntity.setAddedBy(currentUser);
            }
        }
    }
}

