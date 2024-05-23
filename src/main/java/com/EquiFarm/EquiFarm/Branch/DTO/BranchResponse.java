package com.EquiFarm.EquiFarm.Branch.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchResponse {
    private Long id;
    private String solId;
    private String branchName;
    private String region;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
