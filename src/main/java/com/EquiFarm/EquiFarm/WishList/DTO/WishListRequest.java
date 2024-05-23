package com.EquiFarm.EquiFarm.WishList.DTO;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishListRequest {
    private List<Long> farmProductsId;
}
