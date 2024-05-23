package com.EquiFarm.EquiFarm.Branch.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchRequest {
    private String solId;
    private String branchName;
    private String region;
}
