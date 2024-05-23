package com.EquiFarm.EquiFarm.Checkout.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.EquiFarm.EquiFarm.Checkout.CheckoutStatus;
import com.EquiFarm.EquiFarm.DeliveryAddress.DeliveryAddress;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;

import jakarta.persistence.Embedded;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {
    private List<Long> orderId;
    private String orderNote;
//    @Embedded
    private Long deliveryAddressId;
//    private CheckoutStatus status;
//    private List<Long> driversId;
//    private LocalDateTime expectedArrivalDate;
}
