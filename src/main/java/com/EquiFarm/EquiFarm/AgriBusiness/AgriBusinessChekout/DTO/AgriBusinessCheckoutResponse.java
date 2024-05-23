package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout.DTO;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessChekout.AgriBusinessCheckoutStatus;
import com.EquiFarm.EquiFarm.DeliveryAddress.DTO.DeliveryAddressResponse;
import com.EquiFarm.EquiFarm.EscrowWallet.TypeOfTrade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgriBusinessCheckoutResponse {
    private Long id;
    private String orderNote;
    private AgriBusinessCheckoutStatus agriBusinessCheckoutStatus;
    private Double totalAmount;
    private DeliveryAddressResponse deliveryAddress;
    private TypeOfTrade typeOfTrade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    private AgriBusinessOrderResponse agriBusinessOrderResponse;
//    private DeliveryAddressResponse deliveryAddress;
//    private String orderNote;
//    private CheckoutStatus agriBusinessCheckoutStatus;
//    private Double agriBusinessOrderAmount;
//    private Double deliveryFee;
//    private Double totalAmount;
//    private List<DriverResponse> driverResponses;
//    private EscrowWalletResponse wallet;
//    private LocalDateTime expectedArrivalDate;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
}
