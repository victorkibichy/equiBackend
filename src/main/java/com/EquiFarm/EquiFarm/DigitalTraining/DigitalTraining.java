package com.EquiFarm.EquiFarm.DigitalTraining;

import com.EquiFarm.EquiFarm.DigitalTraining.TypeOfDigitalTraining.TypeOfDigitalTraining;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@Table(name = "Digital_Training")
public class DigitalTraining extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "training_name")
    private String trainingName;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "completed", columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "video_url")
    private String videoUrl;

    @Column (name = "image_url")
    private String imageUrl;

    @Column (name = "description")
    private String description;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "typeOfDigitalTraining_id")
//    private TypeOfDigitalTraining typeOfDigitalTraining;


}
