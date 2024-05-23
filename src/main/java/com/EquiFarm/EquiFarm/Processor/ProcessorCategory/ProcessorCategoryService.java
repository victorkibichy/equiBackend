package com.EquiFarm.EquiFarm.Processor.ProcessorCategory;

import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.DTO.ProcessorCategoryRequest;
import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.DTO.ProcessorCategoryResponse;
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
@Transactional
@RequiredArgsConstructor
public class ProcessorCategoryService {
    private final ProcessorCategoryRepository processorCategoryRepository;
    private final ModelMapper modelMapper;

    public ApiResponse<?> addProcessorCategory(ProcessorCategoryRequest processorCategoryRequest) {
        try {
            Optional<ProcessorCategory> processorCategoryOptional = processorCategoryRepository.findByProcessorCategoryAndDeletedFlag(processorCategoryRequest.getProcessorCategory(), Constants.NO);
            if (processorCategoryOptional.isPresent()) {
                return new ApiResponse<>("Processor category already exists.", null, HttpStatus.BAD_REQUEST.value());
            }
            if (processorCategoryRequest.getProcessorCategory() == null) {
                return new ApiResponse<>("Processor category is required.", null, HttpStatus.BAD_REQUEST.value());
            }
            ProcessorCategory processorCategory = new ProcessorCategory();
            processorCategory.setProcessorCategory(processorCategoryRequest.getProcessorCategory());
            processorCategory.setDescription(processorCategory.getDescription());
            ProcessorCategory savedProcessorCategory = processorCategoryRepository.save(processorCategory);
            ProcessorCategoryResponse processorCategoryResponse = modelMapper.map(savedProcessorCategory, ProcessorCategoryResponse.class);

            return new ApiResponse<>("Processor category added successfully.", processorCategoryResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {
            log.error("Adding new processor category error: ", e);
            return new ApiResponse<>("An error occurred while adding a processor category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllProcessorCategories(){
        try {
            List<ProcessorCategory> processorCategoryList = processorCategoryRepository.findByDeletedFlag(Constants.NO);

            List<ProcessorCategoryResponse> processorCategoryResponses = processorCategoryList.stream()
                    .map(processorCategory -> modelMapper.map(processorCategory, ProcessorCategoryResponse.class)).toList();

            return new ApiResponse<>("Processor categories fetched Successfully.", processorCategoryResponses,
                    HttpStatus.OK.value());

        } catch (Exception e){
            log.error("Fetching all processor categories error: ", e);
            return new ApiResponse<>("An error occurred while fetching all processor categories.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getProcessorCategoryById(Long id){
        try {
            Optional<ProcessorCategory> processorCategoryOptional = processorCategoryRepository.findByIdAndDeletedFlag(id, Constants.NO);

            if (processorCategoryOptional.isPresent()) {
                ProcessorCategory processorCategory = processorCategoryOptional.get();

                ProcessorCategoryResponse processorCategoryResponse = modelMapper.map(processorCategory,
                        ProcessorCategoryResponse.class);

                return new ApiResponse<>("Processor category successfully fetched.", processorCategoryResponse,
                        HttpStatus.FOUND.value());
            } else {
                return new ApiResponse<>("Processor category not found.", null, HttpStatus.NOT_FOUND.value());
            }

        }catch (Exception e){
            log.info("Find processor category by id error: ", e);
            return new ApiResponse<>("An Error occurred while fetching a processor category by its id", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateProcessorCategory(ProcessorCategoryRequest processorCategoryRequest,
                                                  Long processorCategoryId){
        try {
            Optional<ProcessorCategory> processorCategoryOptional = processorCategoryRepository.findByIdAndDeletedFlag(processorCategoryId, Constants.NO);
            if (processorCategoryOptional.isPresent()) {
                ProcessorCategory processorCategory = processorCategoryOptional.get();
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorCategoryRequest.getDescription(),
                        processorCategory::setDescription,
                        processorCategory::getDescription);
                if (processorCategoryRequest.getProcessorCategory() != null
                        && processorCategoryRequest.getProcessorCategory().length() > 0
                        && !Objects.equals(processorCategory.getProcessorCategory(),
                        processorCategoryRequest.getProcessorCategory())) {
                    Optional<ProcessorCategory> processorCategoryOptional1 = processorCategoryRepository
                            .findByProcessorCategoryAndDeletedFlag(processorCategoryRequest.getProcessorCategory(),
                                    Constants.NO);

                    if (processorCategoryOptional1.isPresent()) {
                        return new ApiResponse<>("Processor category already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    processorCategory.setProcessorCategory(processorCategoryRequest.getProcessorCategory());

                }
                ProcessorCategory updatedProcessorCategory = processorCategoryRepository.save(processorCategory);

                ProcessorCategoryResponse processorCategoryResponse = modelMapper.map(updatedProcessorCategory,
                        ProcessorCategoryResponse.class);
                return new ApiResponse<>("Processor category updated successfully", processorCategoryResponse,
                        HttpStatus.OK.value());

            }else {
                return new ApiResponse<>("Processor category not found", null,
                        HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e){
            log.info("Updating Category Error: ", e);
            return new ApiResponse<>("Error updating a processor category", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteProcessorCategory(Long id){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<ProcessorCategory> processorCategoryOptional = processorCategoryRepository
                    .findByIdAndDeletedFlag(id, Constants.NO);

            if (processorCategoryOptional.isPresent()) {
                ProcessorCategory processorCategory = processorCategoryOptional.get();
                processorCategory.setDeletedAt(LocalDateTime.now());
                processorCategory.setDeletedBy(currentUser);
                processorCategory.setDeletedFlag(Constants.YES);
                processorCategoryRepository.save(processorCategory);

                return new ApiResponse<>("Processor category successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Processor category not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e){
            log.info("Deleting a processor category error: ", e);
            return new ApiResponse<>("Error deleting a processor category", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
