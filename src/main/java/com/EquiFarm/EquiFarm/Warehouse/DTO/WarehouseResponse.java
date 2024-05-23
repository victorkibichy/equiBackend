package com.EquiFarm.EquiFarm.Warehouse.DTO;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.CordinatesDTO;
import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainResponse;
import com.EquiFarm.EquiFarm.utils.Gender;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponse {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private String bio;
    private Gender gender;
    private String licenceNumber;
    private String warehouseName;
    private String warehouseEmail;
    private String unitMeasurement;
    private String warehousePhoneNumber;
    private Boolean warehouseVerified;
    private String warehouseCode;
    private String profilePicture;
    private Double capacity;
    private Double unitPrice;
    private Double leasePeriod;
    private Double averageRating;
    private List<ValueChainResponse> valueChains;
    @Embedded
    private CordinatesDTO warehouseLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
