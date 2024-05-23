package com.EquiFarm.EquiFarm.ValueChain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueChainResponse {
    private Long id;
    private String valueChain;
    private String description;
}
