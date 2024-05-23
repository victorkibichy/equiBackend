package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO;


import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DTO.DisplayImagesRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FarmProductsRequest {
    // private String productName;
    private Long typeOfProductId;
    private Long productCategoryId;
    private List<DisplayImagesRequest>  displayImages;
    private String productImage;
    private Double latitude;
    private Double longitude;
    private Double unitsAvailable;
    private Double pricePerUnit;
    private String description;
    private Boolean isVerified;
    private Boolean onStock;

    private Boolean isPreListed;
    private LocalDate availabilityDate;

    private List<Long> valueChainIds;
//    private Double amount;
    private LocalDate expirationDate;
    private Double discount;

}
