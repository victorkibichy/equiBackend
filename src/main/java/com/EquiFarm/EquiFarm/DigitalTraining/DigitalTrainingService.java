package com.EquiFarm.EquiFarm.DigitalTraining;

import com.EquiFarm.EquiFarm.DigitalTraining.DTO.DigitalTrainingUserRequest;
import com.EquiFarm.EquiFarm.DigitalTraining.DTO.DigitalTrainingUserResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.UserRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DigitalTrainingService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final DigitalTrainingRepository digitalTrainingRepository;

    public ApiResponse<?> getAllDigitalTrainings() {
        try {
            List<DigitalTraining> digitalTrainingList = digitalTrainingRepository.findByDeletedFlag(Constants.NO);

            List<DigitalTrainingUserResponse> digitalTrainingUserResponse = digitalTrainingList.stream()
                    .map(digitalTrainings -> modelMapper.map(digitalTrainings, DigitalTrainingUserResponse.class))
                    .collect(Collectors.toList());
            return new ApiResponse<>("Digital Trainings fetched successfully", digitalTrainingUserResponse,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching digital trainings.", e);

            return new ApiResponse<>("An error occurred while fetching digital trainings.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> getDigitalTrainingById(Long digitalTrainingId){
        try {
            Optional<DigitalTraining> digitalTrainingOptional = digitalTrainingRepository.findByIdAndDeletedFlag(digitalTrainingId, Constants.NO);
            if(digitalTrainingOptional.isEmpty()){
                return new ApiResponse<>("Digital Training Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            DigitalTraining digitalTraining = digitalTrainingOptional.get();
            DigitalTrainingUserResponse digitalTrainingUserResponse = modelMapper.map(digitalTraining, DigitalTrainingUserResponse.class);
            return new ApiResponse<>("Success retrieving digital training by id", digitalTrainingUserResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error retrieving digital training by id: ",e);
            return new ApiResponse<>("Error retrieving digital training by id", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> createDigitalTraining(DigitalTrainingUserRequest digitalTrainingUserRequest){
        try {
            if (digitalTrainingUserRequest.getTrainingName().isEmpty()){
                return new ApiResponse<>("Training Name is required", null, HttpStatus.BAD_REQUEST.value());
            }
            if (digitalTrainingRepository.existsByTrainingName(digitalTrainingUserRequest.getTrainingName())){
                return new ApiResponse<>("Training name already exists", null, HttpStatus.BAD_REQUEST.value());
            }
            DigitalTraining digitalTraining = new DigitalTraining();
            digitalTraining.setTrainingName(digitalTrainingUserRequest.getTrainingName());
            digitalTraining.setDescription(digitalTrainingUserRequest.getDescription());
            digitalTraining.setStartDate(digitalTrainingUserRequest.getStartDate());
            digitalTraining.setCompletionDate(digitalTrainingUserRequest.getCompletionDate());
            digitalTraining.setIsCompleted(digitalTrainingUserRequest.getCompleted());
            digitalTraining.setImageUrl(digitalTrainingUserRequest.getImageUrl());
            digitalTraining.setVideoUrl(digitalTrainingUserRequest.getVideoUrl());
            DigitalTraining savedDigitalTraining = digitalTrainingRepository.save(digitalTraining);
            DigitalTrainingUserResponse digitalTrainingUserResponse = modelMapper.map(savedDigitalTraining, DigitalTrainingUserResponse.class);
            return new ApiResponse<>("Success adding new digital training", digitalTrainingUserResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error occurred creating digital training", e);
            return new ApiResponse<>("Error occurred creating an expertise",null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> updateDigitalTraining(Long digitalTrainingId, DigitalTrainingUserRequest digitalTrainingUserRequest){
        try {
            Optional<DigitalTraining> digitalTrainingOptional = digitalTrainingRepository.findByIdAndDeletedFlag(digitalTrainingId, Constants.NO);
            if (digitalTrainingOptional.isEmpty()){
                return new ApiResponse<>("Digital training not found", null, HttpStatus.NOT_FOUND.value());
            }
            DigitalTraining digitalTraining = digitalTrainingOptional.get();
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(digitalTrainingUserRequest.getDescription(),
                    digitalTraining::setDescription,digitalTraining::getDescription);
//            DigitalTraining digitalTraining = digitalTrainingOptional.get();
//            FieldUpdateUtil.updateFieldIfNotNullAndChanged(digitalTrainingUserRequest.getStartDate(),
//                    digitalTraining::setStartDate,digitalTraining::getStartDate);

            if (digitalTrainingRepository.existsByTrainingName(digitalTrainingUserRequest.getTrainingName())){
                return new ApiResponse<>("Training name already exists", null, HttpStatus.BAD_REQUEST.value());
            }
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(digitalTrainingUserRequest.getTrainingName(),
                    digitalTraining::setTrainingName, digitalTraining::getTrainingName);
            DigitalTraining savedDigitalTraining = digitalTrainingRepository.save(digitalTraining);
            DigitalTrainingUserResponse digitalTrainingUserResponse = modelMapper.map(savedDigitalTraining, DigitalTrainingUserResponse.class);
            return new ApiResponse<>("Success Updating an Digital training", digitalTrainingUserResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error updating digital training", e);
            return new ApiResponse<>("Error updating digital training", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteDigitalTraining(Long digitalTrainingId){
        try {
            Optional<DigitalTraining> digitalTrainingOptional = digitalTrainingRepository.findByIdAndDeletedFlag(digitalTrainingId, Constants.NO);
            if(digitalTrainingOptional.isEmpty()){
                return new ApiResponse<>("Digital training not found", null, HttpStatus.NOT_FOUND.value());
            }
            DigitalTraining digitalTraining = digitalTrainingOptional.get();
            digitalTraining.setDeletedFlag(Constants.YES);
            digitalTraining.setDeletedAt(LocalDateTime.now());
           digitalTraining.setDeletedBy(SecurityUtils.getCurrentUser());
            digitalTrainingRepository.save(digitalTraining);
            return new ApiResponse<>("Success deleting an digital training", null, HttpStatus.NO_CONTENT.value());
        } catch (Exception e){
            log.info("Error deleting digital training: ", e);
            return new ApiResponse<>("Error deleting digital training", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }



}