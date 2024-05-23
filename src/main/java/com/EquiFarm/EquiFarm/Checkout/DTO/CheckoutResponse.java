package com.EquiFarm.EquiFarm.Checkout.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.Checkout.CheckoutStatus;
import com.EquiFarm.EquiFarm.DeliveryAddress.DTO.DeliveryAddressResponse;
import com.EquiFarm.EquiFarm.Driver.DTO.DriverResponse;
import com.EquiFarm.EquiFarm.EscrowWallet.DTO.EscrowWalletResponse;
import com.EquiFarm.EquiFarm.EscrowWallet.TypeOfTrade;
import com.EquiFarm.EquiFarm.Order.DTO.OrderResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;

import jakarta.persistence.Embedded;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {
    private Long id;
    private String orderNote;
    private CheckoutStatus status;
    private Double totalAmount;
    private DeliveryAddressResponse deliveryAddress;
    private TypeOfTrade typeOfTrade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //    private OrderResponse order;
    // @Embedded
//    private Double orderAmount;
//    private Double deliveryFee;
    // private Cordinates deliveryAddress;
//    private List<DriverResponse> drivers;
//    private EscrowWalletResponse wallet;
//    private LocalDateTime expectedArrivalDate;
}
