package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessCheckoutRequest {
    private List<Long> agriBusinessOrderId;
//    @Embedded
    private Long deliveryAddressId;
    private String orderNote;
//    private CheckoutStatus agriBusinessCheckoutStatus;
//    private List<Long> driversId;
//    private LocalDateTime expectedArrivalDate;
}
