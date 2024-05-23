package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessWishListRequest {
    private List<Long> agriBusinessProductsId;

}


