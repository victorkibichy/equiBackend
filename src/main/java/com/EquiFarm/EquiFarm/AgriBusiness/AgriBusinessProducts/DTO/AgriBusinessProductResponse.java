package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.DTO;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage.DTO.AgriBusinessDisplayImagesResponse;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductCategory.DTO.AgriBusinessProductCategoryResponse;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductsUnitsOfMeasurements;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.TypeOfAgriBusinessProduct.DTO.TypeOfAgriBusinessProductResponse;
import com.EquiFarm.EquiFarm.AgriBusiness.DTO.AgriBusinessUserResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.SellingPoint;

import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainResponse;
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
public class AgriBusinessProductResponse {
    private Long id;
    private List<AgriBusinessDisplayImagesResponse> agriBusinessDisplayImages;
    private String agriBusinessProductImage;
    private TypeOfAgriBusinessProductResponse typeOfAgriBusinessProduct;
    private AgriBusinessProductCategoryResponse agriBusinessProductCategory;
    private Double latitude;
    private Double longitude;
    private AgriBusinessProductsUnitsOfMeasurements unitOfMeasurements;
    private Double unitsAvailable;
    private Double pricePerUnit;
    private Boolean isVerified;
    private Boolean onStock;
    private SellingPoint sellingPoint;
    private AgriBusinessUserResponse agribusiness;
    private String description;
    private List<ValueChainResponse> valueChains;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

