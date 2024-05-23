package com.EquiFarm.EquiFarm.Rating.FarmerRating;

import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Farmer_Rating")
public class FarmerRating extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Farmer farmer;

    @Column(name = "stars")
    private int stars;

    @Column(name = "comment")
    private String comment;
}
