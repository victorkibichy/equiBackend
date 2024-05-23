package com.EquiFarm.EquiFarm.ServiceProvider.Expertise;

import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.DTO.ExpertiseRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.DTO.ExpertiseResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
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

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExpertiseService {
    private final ExpertiseRepo expertiseRepo;
    private final ModelMapper modelMapper;

    public ApiResponse<?> createExpertise(ExpertiseRequest expertiseRequest){
        try {
            if (expertiseRequest.getExpertiseName().isEmpty()){
                return new ApiResponse<>("Expertise Name is required", null, HttpStatus.BAD_REQUEST.value());
            }
            if (expertiseRepo.existsByExpertiseName(expertiseRequest.getExpertiseName())){
                return new ApiResponse<>("Expertise Name already exists", null, HttpStatus.BAD_REQUEST.value());
            }
            Expertise expertise = new Expertise();
            expertise.setExpertiseName(expertiseRequest.getExpertiseName());
            expertise.setDescription(expertiseRequest.getDescription());
            Expertise savedExpertise = expertiseRepo.save(expertise);
            ExpertiseResponse expertiseResponse = modelMapper.map(savedExpertise, ExpertiseResponse.class);
            return new ApiResponse<>("Success adding new expertise", expertiseResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error occurred creating an expertise", e);
            return new ApiResponse<>("Error occurred creating an expertise",null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getExpertiseById(Long expertiseId){
        try {
            Optional<Expertise> expertiseOptional = expertiseRepo.findByIdAndDeletedFlag(expertiseId, Constants.NO);
            if(expertiseOptional.isEmpty()){
                return new ApiResponse<>("Expertise Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Expertise expertise = expertiseOptional.get();
            ExpertiseResponse expertiseResponse = modelMapper.map(expertise, ExpertiseResponse.class);
            return new ApiResponse<>("Success retrieving expertise by id", expertiseResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error retrieving expertise by id: ",e);
            return new ApiResponse<>("Error retrieving expertise by id", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> fetchAll(){
        try {
            List<Expertise> expertiseList = expertiseRepo.findByDeletedFlag(Constants.NO);
            List<ExpertiseResponse> expertiseResponse = expertiseList
                    .stream()
                    .map(expertise -> modelMapper
                            .map(expertise, ExpertiseResponse.class))
                    .toList();
            return new ApiResponse<>("Success retrieving all expertises", expertiseResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error fetching all expertises:", e);
            return new ApiResponse<>("Error retrieving all expertises", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> updateExpertise(Long expertiseId, ExpertiseRequest expertiseRequest){
        try {
            Optional<Expertise> expertiseOptional = expertiseRepo.findByIdAndDeletedFlag(expertiseId, Constants.NO);
            if (expertiseOptional.isEmpty()){
                return new ApiResponse<>("Expertise not found", null, HttpStatus.NOT_FOUND.value());
            }
            Expertise expertise = expertiseOptional.get();
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(expertiseRequest.getDescription(),
                    expertise::setDescription, expertise::getDescription);

                if (expertiseRepo.existsByExpertiseName(expertiseRequest.getExpertiseName())){
                    return new ApiResponse<>("Expertise Name already exists", null, HttpStatus.BAD_REQUEST.value());
                }
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(expertiseRequest.getExpertiseName(),
                        expertise::setExpertiseName, expertise::getExpertiseName);
                Expertise savedExpertise = expertiseRepo.save(expertise);
                ExpertiseResponse expertiseResponse = modelMapper.map(savedExpertise, ExpertiseResponse.class);
                return new ApiResponse<>("Success Updating an Expertise", expertiseResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error updating an expertise", e);
            return new ApiResponse<>("Error updating expertise", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteExpertise(Long expertiseId){
        try {
            Optional<Expertise> expertiseOptional = expertiseRepo.findByIdAndDeletedFlag(expertiseId, Constants.NO);
            if(expertiseOptional.isEmpty()){
                return new ApiResponse<>("Expertise not found", null, HttpStatus.NOT_FOUND.value());
            }
            Expertise expertise = expertiseOptional.get();
            expertise.setDeletedFlag(Constants.YES);
            expertise.setDeletedAt(LocalDateTime.now());
            expertise.setDeletedBy(SecurityUtils.getCurrentUser());
            expertiseRepo.save(expertise);
            return new ApiResponse<>("Success deleting an expertise", null, HttpStatus.NO_CONTENT.value());
        } catch (Exception e){
            log.info("Error deleting expertise: ", e);
            return new ApiResponse<>("Error deleting expertise", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
