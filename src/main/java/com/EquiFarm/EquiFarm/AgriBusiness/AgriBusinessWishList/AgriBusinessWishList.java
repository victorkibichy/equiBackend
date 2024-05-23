package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList;


import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import com.EquiFarm.EquiFarm.Tracking.TrackingEntity;
import com.EquiFarm.EquiFarm.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "AgriBusiness_WishList")
public class AgriBusinessWishList extends TrackingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "agribusinessproducts_id")
    @Builder.Default
    private List<AgriBusinessProduct> agriBusinessProducts = new ArrayList<>();

}

