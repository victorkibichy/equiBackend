package com.EquiFarm.EquiFarm.Farmer.Farm.Weather;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/farms/weather")
@Tag(name = "Weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("get/by/coordinates/{latitude}/{longitude}")
    @PreAuthorize("hasAuthority('farmer:read')")
    public ResponseEntity<?> getWeatherByCoordinates(@PathVariable("latitude") Double latitude, @PathVariable("longitude") Double longitude) {
        return ResponseEntity.ok(weatherService.getWeatherByCoordinates(latitude, longitude));

    }
}