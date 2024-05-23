package com.EquiFarm.EquiFarm.Farmer.Farm.Weather;

import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {
    @Value("${openweathermap.apiKey}")
    private String apiKey;
    private final RestTemplate restTemplate;
    @Transactional
    public ApiResponse<?> getWeatherByCoordinates(Double latitude, Double longitude) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

                String url = "http://api.openweathermap.org/data/2.5/weather?lat="
                        + latitude + "&lon=" + longitude + "&appid=" + apiKey;

                ResponseEntity<Weather> weatherResponse = restTemplate.exchange(url, HttpMethod.GET,
                        null, Weather.class);

            return new ApiResponse<>("Weather Fetched Successfully.", weatherResponse,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching weather.", e);

            return new ApiResponse<>("An error occurred while fetching weather.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
