package com.EquiFarm.EquiFarm.auth.DTO;


import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignoutRequest {
    private String accessToken;
}
