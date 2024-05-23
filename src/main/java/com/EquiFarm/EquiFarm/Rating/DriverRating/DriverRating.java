package com.EquiFarm.EquiFarm.Rating.DriverRating;

import com.EquiFarm.EquiFarm.Driver.Driver;
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
@Table(name = "Driver_Rating")
public class DriverRating extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")  // Specify the foreign key column
    private User user;

    @ManyToOne
    @JoinColumn(name = "driver_id")   // Specify the foreign key column
    private Driver driver;

    @Column(name = "stars")
    private int stars;

    @Column(name = "comment")
    private String comment;


}
