package com.EquiFarm.EquiFarm.Tracking;

import java.time.LocalDateTime;

import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class TrackingEntity {
    @JsonIgnore
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User addedBy;

    @JsonIgnore
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @JsonIgnore
    @Column(nullable = false)
    private Character updateFlag= Constants.NO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;



    @JsonIgnore
    @Column(nullable = false)
    private Character deletedFlag=Constants.NO;
    @JsonIgnore
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;


    @PreRemove
    protected void onDelete(){
        deletedAt=LocalDateTime.now();
        deletedFlag=Constants.YES;
        deletedBy=getCurrentUser();
    }


    @PrePersist
    protected void onCreate() {
        // createdAt = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        addedBy=getCurrentUser();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updateFlag=Constants.YES;
        updatedBy = getCurrentUser();
    }

//    private User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        System.out.println("Current Tracking User>>>>>: " + authentication.getName());
//
//        if (authentication != null && authentication.getPrincipal() instanceof User) {
//            return (User) authentication.getPrincipal();
//        }
//        return null;
//    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            System.out.println("Current Tracking User>>>>>: " + authentication.getName());

            if (authentication.getPrincipal() instanceof User) {
                return (User) authentication.getPrincipal();
            }
        }

        return null;
    }


}


