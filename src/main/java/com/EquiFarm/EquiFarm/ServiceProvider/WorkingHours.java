package com.EquiFarm.EquiFarm.ServiceProvider;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHours {
    @Column(name = "start_hour")
    private LocalTime startHour;
    @Column(name = "end_hour")
    private LocalTime endHour;
}
