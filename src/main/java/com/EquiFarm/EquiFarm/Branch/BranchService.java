package com.EquiFarm.EquiFarm.Branch;

import com.EquiFarm.EquiFarm.Branch.DTO.BranchRequest;
import com.EquiFarm.EquiFarm.Branch.DTO.BranchResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BranchService {
    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;

    public ApiResponse<?> addBranch(BranchRequest branchRequest){
        try {
            // checking if user is authenticated
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            // checking if branch code is populated
            if (branchRequest.getSolId() == null) {
                return new ApiResponse<>("Branch code is required", null,HttpStatus.BAD_REQUEST.value());
            }

            // checking if branch code already exists
            Optional<Branch> branchOptional = branchRepository.findByDeletedFlagAndSolId(Constants.NO,
                    branchRequest.getSolId());
            if (branchOptional.isPresent()) {
                return new ApiResponse<>("Branch code already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

            // creating branch
            var branch = Branch.builder()
                    .solId(branchRequest.getSolId())
                    .branchName(branchRequest.getBranchName())
                    .region(branchRequest.getRegion())
                    .build();
            var savedBranch = branchRepository.save(branch);

            // or
//            Branch branch = new Branch();
//            branch.setBranchCode(branchRequest.getBranchCode());
//            branch.setBranchName(branch.getBranchName());
//            Branch savedBranch = branchRepository.save(branch);

            // mapping the saved branch object to a BranchResponse using ModelMapper
            BranchResponse branchResponse = modelMapper.map(savedBranch,
                    BranchResponse.class);

            return new ApiResponse<>("Branch added successfully.", branchResponse, HttpStatus.CREATED.value());

        }catch (Exception e){

            log.error("An error occurred while adding the branch.", e);
            return new ApiResponse<>("An error occurred while adding the branch.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> fetchAllBranches(){
        try {
            List<Branch> branchList = branchRepository.findByDeletedFlag(Constants.NO);

            List<BranchResponse> branchResponses = branchList.stream()
                    .map(branch -> modelMapper.map(branch,BranchResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Branches fetched successfully", branchResponses, HttpStatus.OK.value());

        }catch (Exception e){
            log.error("An error occurred while fetching branches.", e);

            return new ApiResponse<>("An error occurred while fetching branches.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getBranchById(Long id){
        try {
            Optional<Branch> branchOptional = branchRepository.findByDeletedFlagAndId(Constants.NO,id);

            if (branchOptional.isPresent()) {

                Branch branch = branchOptional.get();
                BranchResponse branchResponse = modelMapper.map(branch, BranchResponse.class);

                return new ApiResponse<>("Branch fetched successfully.", branchResponse,HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Branch not found.", null,HttpStatus.NOT_FOUND.value());
            }

        }catch (Exception e){
            log.error("An error occurred while fetching branch", e);

            return new ApiResponse<>("An error occurred while fetching branch", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateBranch(BranchRequest branchRequest, Long id){
        try {
            Optional<Branch> branchOptional = branchRepository.findByDeletedFlagAndId(Constants.NO,id);

            if (branchOptional.isPresent()) {
                    Branch branch = branchOptional.get();

                    if (branchRequest.getSolId() != null && branchRequest.getSolId().length() > 0
                            && !Objects.equals(branchRequest.getSolId(), branch.getSolId())) {

                        Optional<Branch> branchSolIdOptional = branchRepository
                                .findByDeletedFlagAndSolId(Constants.NO, branchRequest.getSolId());

                        if (branchSolIdOptional.isPresent()) {
                            return new ApiResponse<>("Branch with this code already exists.",null,
                                    HttpStatus.BAD_REQUEST.value());
                        }

                        branch.setSolId(branchRequest.getSolId());
                    }

                    if (branchRequest.getBranchName() != null && branchRequest.getBranchName().length() > 0
                        && !Objects.equals(branchRequest.getBranchName(), branch.getBranchName())) {

                        Optional<Branch> branchNameOptional = branchRepository
                            .findByDeletedFlagAndBranchName(Constants.NO, branchRequest.getBranchName());

                        if (branchNameOptional.isPresent()) {
                            return new ApiResponse<>("Branch with this name already exists.",null,
                                    HttpStatus.BAD_REQUEST.value());
                        }

                        branch.setBranchName(branchRequest.getBranchName());
                    }

                if (branchRequest.getRegion() != null && branchRequest.getRegion().length() > 0
                        && !Objects.equals(branchRequest.getRegion(), branch.getRegion())) {
                    branch.setRegion(branchRequest.getRegion());
                }

                Branch updatedBranch = branchRepository.save(branch);
                BranchResponse branchResponse = modelMapper.map(updatedBranch, BranchResponse.class);

                return new ApiResponse<>("Branch updated successfully.",branchResponse, HttpStatus.OK.value());

            } else {

                return new ApiResponse<>("Branch not found.",null,HttpStatus.NOT_FOUND.value());
            }
        }catch (Exception e){
            log.error("An error occurred while updating the branch.", e);

            return new ApiResponse<>("An error occurred while updating the branch.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> removeBranch(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null){
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Branch> branchOptional = branchRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (branchOptional.isPresent()) {
                Branch branch = branchOptional.get();

                branch.setDeletedAt(LocalDateTime.now());
                branch.setDeletedBy(currentUser);
                branch.setDeletedFlag(Constants.YES);

                branchRepository.save(branch);

                return new ApiResponse<>("Branch was successfully removed.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Branch not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while removing the branch", e);

            return new ApiResponse<>("An error occurred while removing the branch", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
