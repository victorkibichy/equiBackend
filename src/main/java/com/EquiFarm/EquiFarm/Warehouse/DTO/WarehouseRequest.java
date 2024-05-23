package com.EquiFarm.EquiFarm.Warehouse.DTO;


import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.CordinatesDTO;
import com.EquiFarm.EquiFarm.utils.Gender;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRequest {
    private Long id;
    private UserResponse user;
    private String idNumber;
    private String bio;
    private Gender gender;
    private String licenceNumber;
    private String warehouseName;
    private String warehouseEmail;
    private String warehousePhoneNumber;
    private String warehouseLogo;
    private String warehouseDescription;
    private String profilePicture;
    private Double capacity;
    private Double unitPrice;
    private String unitMeasurement;
    private Boolean warehouseVerified;
    private Double leasePeriod;
    private boolean vacant;
    private List<Long> valueChainIds;
    @Embedded
    private CordinatesDTO businessLocation;
}
