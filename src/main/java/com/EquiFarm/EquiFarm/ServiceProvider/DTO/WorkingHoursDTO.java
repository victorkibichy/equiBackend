package com.EquiFarm.EquiFarm.ServiceProvider.DTO;

import java.time.LocalTime;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkingHoursDTO {
    private LocalTime startHour;
    private LocalTime endHour;
}
