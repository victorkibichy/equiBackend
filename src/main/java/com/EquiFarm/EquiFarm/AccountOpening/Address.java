package com.EquiFarm.EquiFarm.AccountOpening;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Address {
    private String category;
    private String city;
    private String country;
    private String state;
    private Long postalCode;

}
