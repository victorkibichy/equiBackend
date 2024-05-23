package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO;


import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DTO.DisplayImagesResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsUnitsOfMeasurements;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.SellingPoint;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.ProductCategory.DTO.ProductCategoryResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.TypesOfProducts.DTO.TypeOfProductResponse;
import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmProductsResponse {
    private Long id;
    private List<DisplayImagesResponse>  displayImages;
    private String productImage;
    private TypeOfProductResponse typeOfProduct;
    private ProductCategoryResponse category;
    private Double latitude;
    private Double longitude;
    private FarmProductsUnitsOfMeasurements unitOfMeasurements;
    private Double unitsAvailable;
    private Double pricePerUnit;
    private Boolean isVerified;
    private Boolean onStock;
    private SellingPoint sellingPoint;
    private FarmersResponse farmer;
    private String description;
    private Boolean isPreListed;
    private LocalDate availabilityDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ValueChainResponse> valueChains;
//    private Double amount;
    private LocalDate expirationDate;
    private Double discount;

}
