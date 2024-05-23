package com.EquiFarm.EquiFarm.ValueChain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueChainRequest {
    private String valueChain;
    private String description;
}
