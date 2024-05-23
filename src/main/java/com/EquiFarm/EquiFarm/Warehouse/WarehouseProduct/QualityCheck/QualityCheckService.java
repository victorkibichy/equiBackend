package com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck;

import com.EquiFarm.EquiFarm.Admin.Admin;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.DTO.QualityCheckRequest;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseProduct.QualityCheck.DTO.QualityCheckResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QualityCheckService {
    private final QualityCheckRepo qualityCheckRepo;
    private final ModelMapper modelMapper;

//    public ApiResponse<?> addQualityCheck(QualityCheckRequest qualityCheckRequest){
//        try {
//            if (qualityCheckRepo.existsByCheckName(qualityCheckRequest.getCheckName())){
//                return new ApiResponse<>("Quality Check already exists", null, HttpStatus.BAD_REQUEST.value());
//            }
//            QualityCheck qualityCheck = new QualityCheck();
//            qualityCheck.setCheckName(qualityCheckRequest.getCheckName());
//            qualityCheck.setDescription(qualityCheckRequest.getDescription());
//            QualityCheck savedCheck = qualityCheckRepo.save(qualityCheck);
//            QualityCheckResponse qualityCheckResponse = modelMapper.map(savedCheck, QualityCheckResponse.class);
//            return new ApiResponse<>("Success adding a quality check", qualityCheckResponse, HttpStatus.OK.value());
//
//        } catch (Exception e){
//            log.info("Error occurred adding a quality check: ", e);
//            return new ApiResponse<>("Error occurred adding a quality check", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//
//    }

    public ApiResponse<?> updateQualityCheck(Long qualityCheckId,QualityCheckRequest qualityCheckRequest){
        try {
            Optional<QualityCheck> qualityCheckOptional = qualityCheckRepo.findByIdAndDeletedFlag(qualityCheckId, Constants.NO);
            if(qualityCheckOptional.isEmpty()){
                return new ApiResponse<>("Quality Check not found", null, HttpStatus.NOT_FOUND.value());
            }
            QualityCheck qualityCheck = qualityCheckOptional.get();
            if (qualityCheckRepo.existsByCheckName(qualityCheckRequest.getCheckName())){
                return new ApiResponse<>("Quality Check Name already exists", null, HttpStatus.BAD_REQUEST.value());
            }
            if(qualityCheckRequest.getCheckName() != null
                    && !Objects.equals(qualityCheckRequest.getCheckName(), qualityCheck.getCheckName())){
                qualityCheck.setCheckName(qualityCheckRequest.getCheckName());
            }

            if(qualityCheckRequest.getDescription() != null
                    && !Objects.equals(qualityCheckRequest.getDescription(), qualityCheck.getDescription())){
                qualityCheck.setDescription(qualityCheckRequest.getDescription());
            }
            if(qualityCheckRequest.getQualityCheckResult() != null
                    && !Objects.equals(qualityCheckRequest.getQualityCheckResult(), qualityCheck.getQualityCheckResult())){
                qualityCheck.setQualityCheckResult(qualityCheckRequest.getQualityCheckResult());
            }
            if(qualityCheckRequest.getComments() != null
                    && !Objects.equals(qualityCheckRequest.getComments(), qualityCheck.getComments())){
                qualityCheck.setComments(qualityCheckRequest.getComments());
            }
            QualityCheck updatedCheck = qualityCheckRepo.save(qualityCheck);
            QualityCheckResponse qualityCheckResponse = modelMapper.map(updatedCheck, QualityCheckResponse.class);
            return new ApiResponse<>("Success Updating Quality Check", qualityCheckResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error occurred updating a quality check: ", e);
            return new ApiResponse<>("Error occurred updating a quality check", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAll(){
        try {
            List<QualityCheck> qualityCheckList = qualityCheckRepo.findByDeletedFlag(Constants.NO);
            List<QualityCheckResponse> qualityCheckResponses = qualityCheckList
                    .stream()
                    .map(check-> modelMapper
                            .map(check, QualityCheckResponse.class))
                    .toList();
            return new ApiResponse<>("Success Fetching Quality Check", qualityCheckResponses, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error occurred fetching quality checks: ", e);
            return new ApiResponse<>("Error occurred fetching quality checks", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deletedQualityCheck(Long checkId){
        try {
            Optional<QualityCheck> qualityCheckOptional = qualityCheckRepo.findByIdAndDeletedFlag(checkId, Constants.NO);
            if (qualityCheckOptional.isEmpty()) {
                return new ApiResponse<>("Quality Check not found", null, HttpStatus.NOT_FOUND.value());
            }
            User currentUser = SecurityUtils.getCurrentUser();
            QualityCheck qualityCheck = qualityCheckOptional.get();
            qualityCheck.setDeletedAt(LocalDateTime.now());
            qualityCheck.setDeletedBy(currentUser);
            qualityCheck.setDeletedFlag(Constants.YES);
            qualityCheckRepo.save(qualityCheck);
            return new ApiResponse<>("Success Deleting Quality check", null, HttpStatus.NO_CONTENT.value());

        }catch (Exception e){
            log.info("An error occurred deleting a quality check");
            return new ApiResponse<>("An Error occurred deleting a quality check", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
