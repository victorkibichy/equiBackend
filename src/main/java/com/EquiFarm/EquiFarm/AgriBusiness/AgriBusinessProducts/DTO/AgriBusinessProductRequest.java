package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage.DTO.AgriBusinessDisplayImagesRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductsUnitsOfMeasurements;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessProductRequest {
    // private String productName;
    private Long typeOfAgriBusinessProductId;
    private Long agriBusinessProductCategoryId;
    private List<AgriBusinessDisplayImagesRequest> agriBusinessDisplayImages;
    private String agriBusinessProductImage;
    private Double latitude;
    private Double longitude;
    private Double unitsAvailable;
    private Double pricePerUnit;
    private String description;
    private Boolean isVerified;
    private Boolean onStock;
    private AgriBusinessProductsUnitsOfMeasurements unitOfMeasurements;
    private List<Long> valueChainIds;
}




