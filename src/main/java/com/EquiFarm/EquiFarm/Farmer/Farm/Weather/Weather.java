package com.EquiFarm.EquiFarm.Farmer.Farm.Weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weather {

    private Coordinates coord;
    private List<WeatherInfo> weather;
    private String base;
    private MainWeatherData main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private int id;
    private String name;

    @Data
    public static class Coordinates {
        private double lon;
        private double lat;
    }

    @Data
    public static class WeatherInfo {
        private int id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class MainWeatherData {
        private double temp;
        private double feels_like;
        private double temp_min;
        private double temp_max;
        private int pressure;
        private int humidity;
    }

    @Data
    public static class Wind {
        private double speed;
        private int deg;
    }

    @Data
    public static class Clouds {
        private int all;
    }

    @Data
    public static class Sys {
        private String country;
        private long sunrise;
        private long sunset;
    }
}
