package com.EquiFarm.EquiFarm.DeliveryAddress;

import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Delivery_Address")
public class DeliveryAddress extends TrackingEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "address_name", length = 96, nullable = false)
    private String addressName;

    @Column(name = "latitude", nullable = true)
    @Builder.Default
    private Double latitude = -0.0236;

    @Column(name = "longitude", nullable = true)
    @Builder.Default
    private Double longitude = 37.9062;

    @Column(name = "default_address", columnDefinition = "boolean default false")
    @Builder.Default
    private boolean isDefaultAddress=false;

    @Column(name = "address", length = 256)
    private String address;    

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;
}
