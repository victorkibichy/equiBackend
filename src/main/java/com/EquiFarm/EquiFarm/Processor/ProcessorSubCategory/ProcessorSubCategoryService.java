package com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory;

import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.DTO.ProcessorCategoryResponse;
import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategory;
import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategoryRepository;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.DTO.ProcessorSubCategoryRequest;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.DTO.ProcessorSubCategoryResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
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
public class ProcessorSubCategoryService {
    private final ProcessorSubCategoryRepository processorSubCategoryRepository;

    private final ProcessorCategoryRepository processorCategoryRepository;
    private final ModelMapper modelMapper;

    public ApiResponse<?> addProcessorSubCategory(ProcessorSubCategoryRequest processorSubCategoryRequest) {
        try {
            Optional<ProcessorSubCategory> processorSubCategoryOptional = processorSubCategoryRepository
                    .findByProcessorSubCategoryAndDeletedFlag(processorSubCategoryRequest
                            .getProcessorSubCategory(), Constants.NO);
            if (processorSubCategoryOptional.isPresent()) {
                return new ApiResponse<>("Processor Sub Category already exists.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            if (processorSubCategoryRequest.getProcessorSubCategory() == null) {
                return new ApiResponse<>("Processor Sub Category is required.", null,
                        HttpStatus.BAD_REQUEST.value());
            }
            Optional<ProcessorCategory> processorCategory = processorCategoryRepository.findByIdAndDeletedFlag(
                    processorSubCategoryRequest.getProcessorCategoryId(), Constants.NO);

            System.out.println("Processor Category: "+ processorCategory);

            if (processorCategory.isPresent()) {
                ProcessorSubCategory processorSubCategory = new ProcessorSubCategory();
                processorSubCategory.setProcessorSubCategory(processorSubCategoryRequest.getProcessorSubCategory());
                processorSubCategory.setDescription(processorSubCategory.getDescription());
                processorSubCategory.setProcessorCategory(processorCategory.get());
                ProcessorSubCategory savedProcessorSubCategory = processorSubCategoryRepository.save(processorSubCategory);
                ProcessorSubCategoryResponse processorSubCategoryResponse = modelMapper.map(savedProcessorSubCategory,
                        ProcessorSubCategoryResponse.class);

                return new ApiResponse<>("Processor SubCategory added successfully.", processorSubCategoryResponse,
                        HttpStatus.CREATED.value());
            } else {
                return  new ApiResponse<>("Processor Category does not exist", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("Adding new Processor Sub Category error: ", e);
            return new ApiResponse<>("An error occurred while adding a processor sub category.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllProcessorSubCategories(){
        try {
            List<ProcessorSubCategory> processorSubCategoryList = processorSubCategoryRepository
                    .findByDeletedFlag(Constants.NO);

            List<ProcessorSubCategoryResponse> processorSubCategoryResponse = processorSubCategoryList.stream()
                    .map(processorSubcategory -> modelMapper.map(processorSubcategory,
                            ProcessorSubCategoryResponse.class)).toList();

            return new ApiResponse<>("Processor sub categories fetched Successfully.",
                    processorSubCategoryResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.error("Fetching all processor Sub Categories error: ", e);
            return new ApiResponse<>("An Error occurred while fetching all processor sub categories.",
                    null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getByProcessorCategory(ProcessorCategory processorCategory){
        try {
            List<ProcessorSubCategory> processorSubCategoryList = processorSubCategoryRepository
                    .findByProcessorCategory(processorCategory);
            if (processorSubCategoryList.size() > 0){
                List<ProcessorSubCategoryResponse> processorSubCategoryResponse = processorSubCategoryList.stream()
                        .map(processorSubCategory -> modelMapper.map(processorSubCategory,
                                ProcessorSubCategoryResponse.class)).toList();
                return new ApiResponse<>("Successfully Fetched Processor Sub Categories",
                        processorSubCategoryResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Processor Sub Categories not found", null,
                        HttpStatus.OK.value());
            }

        } catch (Exception e){
            log.info("Error fetching processor sub categories:", e);
            return new ApiResponse<>("Error occurred when fetching processor sub categories",
                    null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getProcessorSubCategoryById(Long id){
        try {
            Optional<ProcessorSubCategory> processorSubCategoryOptional = processorSubCategoryRepository.
                    findByIdAndDeletedFlag(id, Constants.NO);

            if (processorSubCategoryOptional.isPresent()) {
                ProcessorSubCategory processorSubCategory = processorSubCategoryOptional.get();

                ProcessorSubCategoryResponse processorSubCategoryResponse = modelMapper.map(processorSubCategory,
                        ProcessorSubCategoryResponse.class);

                return new ApiResponse<>("Processor SubCategory successfully fetched.",
                        processorSubCategoryResponse, HttpStatus.FOUND.value());
            } else {
                return new ApiResponse<>("Processor SubCategory not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        }catch (Exception e){
            log.info("Find processor subCategory by id error: ", e);
            return new ApiResponse<>("An Error occurred while fetching a processor subCategory by its id",
                    null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> updateProcessorSubCategory(ProcessorSubCategoryRequest processorSubCategoryRequest,
                                            Long processorSubCategoryId){
        try {
            Optional<ProcessorSubCategory> processorSubCategoryOptional = processorSubCategoryRepository
                    .findByIdAndDeletedFlag(processorSubCategoryId, Constants.NO);
            if (processorSubCategoryOptional.isPresent()) {

                ProcessorSubCategory processorSubCategory = processorSubCategoryOptional.get();

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorSubCategoryRequest.getDescription(),
                        processorSubCategory::setDescription,
                        processorSubCategory::getDescription);

                if (processorSubCategoryRequest.getProcessorCategoryId() != null) {
                    Optional<ProcessorCategory> processorCategoryOptional = processorCategoryRepository
                            .findByIdAndDeletedFlag(processorSubCategoryRequest.getProcessorCategoryId(), Constants.NO);

                    if (processorCategoryOptional.isPresent()) {
                        ProcessorCategory processorCategory = processorCategoryOptional.get();
                        processorSubCategory.setProcessorCategory(processorCategory);
                    } else {
                        return new ApiResponse<>("Processor category does not exist.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }

                }

                if (processorSubCategoryRequest.getProcessorSubCategory() != null
                        && processorSubCategoryRequest.getProcessorSubCategory().length() > 0
                        && !Objects.equals(processorSubCategory.getProcessorSubCategory(),
                        processorSubCategoryRequest.getProcessorSubCategory())) {

                    Optional<ProcessorSubCategory> processorSubCategoryOptional1 = processorSubCategoryRepository
                            .findByProcessorSubCategoryAndDeletedFlag(processorSubCategoryRequest
                                    .getProcessorSubCategory(), Constants.NO);

                    if (processorSubCategoryOptional1.isPresent()) {
                        return new ApiResponse<>("Processor SubCategory already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    processorSubCategory.setProcessorSubCategory(processorSubCategoryRequest.getProcessorSubCategory());

                }
                ProcessorSubCategory updatedProcessorCategory = processorSubCategoryRepository.save(processorSubCategory);

                ProcessorCategoryResponse processorCategoryResponse = modelMapper.map(updatedProcessorCategory,
                        ProcessorCategoryResponse.class);
                return new ApiResponse<>("Processor SubCategory updated successfully",
                        processorCategoryResponse, HttpStatus.OK.value());

            }else {
                return new ApiResponse<>("Processor SubCategory not found", null,
                        HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e){
            log.info("Updating Processor SubCategory Error: ", e);
            return new ApiResponse<>("Error updating a processor subCategory", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteProcessorSubCategory(Long id){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<ProcessorSubCategory> processorSubCategoryOptional = processorSubCategoryRepository
                    .findByIdAndDeletedFlag(id, Constants.NO);

            if (processorSubCategoryOptional.isPresent()) {
                ProcessorSubCategory processorSubCategory = processorSubCategoryOptional.get();
                processorSubCategory.setDeletedAt(LocalDateTime.now());
                processorSubCategory.setDeletedBy(currentUser);
                processorSubCategory.setDeletedFlag(Constants.YES);
                processorSubCategoryRepository.save(processorSubCategory);

                return new ApiResponse<>("Processor SubCategory successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Processor SubCategory not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e){
            log.info("Deleting a processor subCategory error: ", e);
            return new ApiResponse<>("Error deleting a processor subCategory", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
