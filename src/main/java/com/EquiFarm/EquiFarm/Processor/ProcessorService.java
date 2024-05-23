package com.EquiFarm.EquiFarm.Processor; // Update the package name

import com.EquiFarm.EquiFarm.Processor.DTO.ProcessorRequest;
import com.EquiFarm.EquiFarm.Processor.DTO.ProcessorResponse;
import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategory;
import com.EquiFarm.EquiFarm.Processor.ProcessorCategory.ProcessorCategoryRepository;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.ProcessorSubCategory;
import com.EquiFarm.EquiFarm.Processor.ProcessorSubCategory.ProcessorSubCategoryRepository;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.ServiceProvider.WorkingHours;
import com.EquiFarm.EquiFarm.ValueChain.ValueChain;
import com.EquiFarm.EquiFarm.ValueChain.ValueChainRepo;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
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
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;


import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProcessorService { // Rename the class to ProcessorService
    private final ProcessorRepository processorRepository; // Update with the Processor repository
    private final ProcessorSubCategoryRepository processorSubCategoryRepository;
    private final ProcessorCategoryRepository processorCategoryRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ValueChainRepo valueChainRepo;

    public ApiResponse<?> fetchProcessorProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Processor> processorOptional = processorRepository.findByUserIdAndDeletedFlag(currentUser.getId(), Constants.NO);
            if (processorOptional.isPresent()) {
                Processor processor = processorOptional.get();
                ProcessorResponse processorResponse = modelMapper.map(processor, ProcessorResponse.class);
                return new ApiResponse<>("Success fetching Processor's Profile", processorResponse, HttpStatus.FOUND.value());
            } else {
                return new ApiResponse<>("Processor Profile not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.info("Error fetching Processor's Profile", e);
            return new ApiResponse<>("Error fetching Processor's Profile", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getProcessorByUserId(Long id) {
        try {
            Optional<Processor> processorOptional = processorRepository.findByUserIdAndDeletedFlag(id, Constants.NO);

            if (processorOptional.isPresent()) {
                Processor processor = processorOptional.get();

                System.out.println("Processor Found: " + processor);

                ProcessorResponse processorResponse = modelMapper.map(processor, ProcessorResponse.class);

                return new ApiResponse<>("Processor fetched successfully", processorResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Processor not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching processor.", e);

            return new ApiResponse<>("An error occurred while fetching Processor.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllProcessors() {
        try {
            List<Processor> processorList = processorRepository.findByDeletedFlag(Constants.NO);

            List<ProcessorResponse> processorResponseList = processorList.stream()
                    .map(processor -> modelMapper.map(processor, ProcessorResponse.class))
                    .toList();

            return new ApiResponse<>("Processors fetched successfully", processorResponseList,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred fetching all processors", e);
            return new ApiResponse<>("Error occurred fetching all processors", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllProcessorsInValueChain(Long valueChainId) {
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()) {
                return new ApiResponse<>("Value chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            List<Processor> processorList = processorRepository.findByValueChainsContaining(valueChain);
            List<ProcessorResponse> processorResponses = processorList
                    .stream()
                    .map(processor -> modelMapper
                            .map(processor, ProcessorResponse.class))
                    .toList();
            return new ApiResponse<>("Processors fetched successfully", processorResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error occurred fetching Processors", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getByProcessorSubCategory(
             ProcessorSubCategory processorSubCategory) {
        try {
            List<Processor> processorList = processorRepository
                    .findByProcessorSubCategoryAndDeletedFlag(processorSubCategory, Constants.NO);

            List<ProcessorResponse> processorResponses = processorList.stream()
                    .map(processor -> modelMapper.map(processor, ProcessorResponse.class))
                    .toList();

            return new ApiResponse<>("Processors fetched successfully", processorResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error fetching processors by ProcessorCategory and ProcessorSubCategory", e);
            return new ApiResponse<>("Error fetching processors by ProcessorCategory and ProcessorSubCategory", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getByProcessorCategory(ProcessorCategory processorCategory) {
        try {
            List<Processor> processorList = processorRepository.findByProcessorCategoryAndDeletedFlag(processorCategory, Constants.NO);

            List<ProcessorResponse> processorResponses = processorList.stream()
                    .map(processor -> modelMapper.map(processor, ProcessorResponse.class))
                    .toList();

            return new ApiResponse<>("Processors fetched successfully by ProcessorCategory", processorResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error fetching processors by ProcessorCategory", e);
            return new ApiResponse<>("Error fetching processors by ProcessorCategory", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }



    public ApiResponse<?> processorProfileUpdate(ProcessorRequest processorRequest) {
            try {
                User currentUser = SecurityUtils.getCurrentUser();
                if (currentUser == null) {
                    return new ApiResponse<>("Access denied. User is not authenticated.", null,
                            HttpStatus.UNAUTHORIZED.value());
                }

                Optional<Processor> processorOptional = processorRepository
                        .findByUserIdAndDeletedFlag(currentUser.getId(), Constants.NO);

                if (processorOptional.isPresent()) {
                    Processor processor = processorOptional.get();

                    if (processorRequest.getIdNumber() != null
                            && processorRequest.getIdNumber().length() > 0
                            && !Objects.equals(processorRequest.getIdNumber(), processor.getIdNumber())) {
                        Optional<Processor> processorIdOptional = processorRepository
                                .findByIdNumber(processorRequest.getIdNumber());
                        if (processorIdOptional.isPresent()) {
                            return new ApiResponse<>("User with this Id Number already exists.", null, HttpStatus.FORBIDDEN.value());
                        }
                        processor.setIdNumber(processorRequest.getIdNumber());
                    }
                    if (processorRequest.getProcessorSubCategoryId() != null) {
                        Optional<ProcessorSubCategory> processorSubCategoryOptional = processorSubCategoryRepository
                                .findByIdAndDeletedFlag(processorRequest.getProcessorSubCategoryId(), Constants.NO);
                        if (processorSubCategoryOptional.isEmpty()) {
                            return new ApiResponse<>("ProcessorSubCategory Not Found", null, HttpStatus.NOT_FOUND.value());
                        }
                        ProcessorSubCategory processorSubCategory = processorSubCategoryOptional.get();
                        processor.setProcessorCategory(processorSubCategory.getProcessorCategory());
                        processor.setProcessorSubCategory(processorSubCategory);
                    }
                    if (processorRequest.getValueChainIds() != null) {
                        if (processorRequest.getValueChainIds().isEmpty()) {
                            return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                        }
                        // Replace existing value chains
                        Set<Long> newValueChainIds = new HashSet<>(processorRequest.getValueChainIds());

                        Iterator<ValueChain> iterator = processor.getValueChains().iterator();
                        while (iterator.hasNext()) {
                            ValueChain existingValueChain = iterator.next();
                            if (!newValueChainIds.contains(existingValueChain.getId())) {
                                iterator.remove();
                                existingValueChain.getProcessorList().remove(processor);
                                valueChainRepo.save(existingValueChain);
                            }
                        }
                        for (Long valueId : processorRequest.getValueChainIds()) {
                            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                            if (valueChainOptional.isEmpty()) {
                                return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                            }
                            ValueChain valueChain = valueChainOptional.get();
                            if (!processor.getValueChains().contains(valueChain)) {
                                processor.getValueChains().add(valueChain);
                                valueChain.getProcessorList().add(processor);
                                valueChainRepo.save(valueChain);
                            }
                        }
                    }
                    processor.setProcessorVerified(false);
                    updateProcessorFields(processor, processorRequest);
                    updateWorkingHours(processor, processorRequest);
                    updateBusinessLocation(processor, processorRequest);
                    updateWorkingDays(processor, processorRequest);

                    Processor updatedProcessor = processorRepository.save(processor);

                    ProcessorResponse processorResponse = modelMapper.map(updatedProcessor,
                            ProcessorResponse.class);

                    return new ApiResponse<>("Processor profile was successfully updated.",
                            processorResponse, HttpStatus.OK.value());
                } else {
                    return new ApiResponse<>("Processor not found", null, HttpStatus.NOT_FOUND.value());
                }
            } catch (Exception e) {
                log.error("An error occurred while updating processor's profile.", e);
                return new ApiResponse<>("An error occurred while updating processor's profile.", null,
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }


    private void updateWorkingDays(Processor processor,
                                   ProcessorRequest processorRequest) {
        if (processorRequest.getWorkingDays() != null) {
            processor.setWorkingDays(processorRequest.getWorkingDays());
        }
    }

    private void updateWorkingHours(Processor processor,
                                    ProcessorRequest processorRequest) {
        if (processorRequest.getWorkingHours() != null) {
            WorkingHours workingHours = processor.getWorkingHours();
            if (workingHours == null) {
                workingHours = new WorkingHours();
            }
            workingHours.setStartHour(processorRequest.getWorkingHours().getStartHour());
            workingHours.setEndHour(processorRequest.getWorkingHours().getEndHour());
            processor.setWorkingHours(workingHours);
        }
    }

    private void updateBusinessLocation(Processor processor,
                                        ProcessorRequest processorRequest) {
        if (processorRequest.getBusinessLocation() != null) {
            Cordinates cordinates = processor.getProcessorLocation();
            if (cordinates == null) {
                cordinates = new Cordinates();
            }
            cordinates.setLatitude(processorRequest.getBusinessLocation().getLatitude());
            cordinates.setLongitude(processorRequest.getBusinessLocation().getLongitude());
            processor.setProcessorLocation(cordinates);
        }
    }

    public ApiResponse<?> updateProcessorByProcessorId(ProcessorRequest processorRequest, Long processorId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. Unauthenticated User", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Processor> processorOptional = processorRepository.findByIdAndDeletedFlag(processorId, Constants.NO);
            if (processorOptional.isPresent()) {
                Processor processor = processorOptional.get();

                // Check and update ID Number
                if (processorRequest.getIdNumber() != null
                        && !Objects.equals(processorRequest.getIdNumber(), processor.getIdNumber())) {
                    Optional<Processor> processorIdOptional = processorRepository.findByIdNumber(processorRequest.getIdNumber());
                    if (processorIdOptional.isPresent()) {
                        return new ApiResponse<>("ID Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    processor.setIdNumber(processorRequest.getIdNumber());
                }

                // Check and update Phone Number
                if (processorRequest.getProcessorPhoneNumber() != null
                        && !Objects.equals(processorRequest.getProcessorPhoneNumber(), processor.getProcessorPhoneNumber())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(processorRequest.getProcessorPhoneNumber());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    processor.getUser().setPhoneNo(processorRequest.getProcessorPhoneNumber());
                }

                // Check and update Email
                if (processorRequest.getProcessorEmail() != null
                        && !Objects.equals(processorRequest.getProcessorEmail(), processor.getProcessorEmail())) {
                    Optional<User> userOptional = userRepository.findByEmail(processorRequest.getProcessorEmail());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Email already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    processor.getUser().setEmail(processorRequest.getProcessorEmail());
                }

                // Check and update Category
                if (processorRequest.getProcessorCategoryId() != null) {
                    Optional<ProcessorCategory> processorCategoryOptional = processorCategoryRepository.findByIdAndDeletedFlag(
                            processorRequest.getProcessorCategoryId(), Constants.NO);
                    if (processorCategoryOptional.isEmpty()) {
                        return new ApiResponse<>("ProcessorCategory not found.", null, HttpStatus.NOT_FOUND.value());
                    }
                    ProcessorCategory processorCategory = processorCategoryOptional.get();
                    processor.setProcessorCategory(processorCategory);
                }

                // Check and update SubCategory
                if (processorRequest.getProcessorSubCategoryId() != null) {
                    Optional<ProcessorSubCategory> processorSubCategoryOptional = processorSubCategoryRepository.findByIdAndDeletedFlag(
                            processorRequest.getProcessorSubCategoryId(), Constants.NO);
                    if (processorSubCategoryOptional.isEmpty()) {
                        return new ApiResponse<>("ProcessorSubCategory not found.", null, HttpStatus.NOT_FOUND.value());
                    }
                    ProcessorSubCategory processorSubCategory = processorSubCategoryOptional.get();
                    processor.setProcessorSubCategory(processorSubCategory);
                }

                // Check and update Value Chains
                if (processorRequest.getValueChainIds() != null) {
                    if (processorRequest.getValueChainIds().isEmpty()) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // Replace existing value chains
                    Set<Long> newValueChainIds = new HashSet<>(processorRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = processor.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getProcessorList().remove(processor);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : processorRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!processor.getValueChains().contains(valueChain)) {
                            processor.getValueChains().add(valueChain);
                            valueChain.getProcessorList().add(processor);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                // Update other fields
                updateProcessorFields(processor, processorRequest);
                updateWorkingHours(processor, processorRequest);
                updateBusinessLocation(processor, processorRequest);
                updateWorkingDays(processor, processorRequest);

                // Save the updated processor
                Processor updatedProcessor = processorRepository.save(processor);

                ProcessorResponse processorResponse = modelMapper.map(updatedProcessor, ProcessorResponse.class);

                return new ApiResponse<>("Processor profile successfully updated.",
                        processorResponse, HttpStatus.OK.value());
            }

            return new ApiResponse<>("Processor not found", null, HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            log.info("Error Updating Processor by Processor ID", e);
            return new ApiResponse<>("Error Updating Processor by Processor ID", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateProcessorByUserId(ProcessorRequest processorRequest, Long userId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. Unauthenticated User", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Processor> processorOptional = processorRepository.findByUserIdAndDeletedFlag(userId, Constants.NO);
            if (processorOptional.isPresent()) {
                Processor processor = processorOptional.get();

                // Check and update ID Number
                if (processorRequest.getIdNumber() != null
                        && !Objects.equals(processorRequest.getIdNumber(), processor.getIdNumber())) {
                    Optional<Processor> processorIdOptional = processorRepository.findByIdNumber(processorRequest.getIdNumber());
                    if (processorIdOptional.isPresent()) {
                        return new ApiResponse<>("ID Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    processor.setIdNumber(processorRequest.getIdNumber());
                }

                // Check and update Phone Number
                if (processorRequest.getProcessorPhoneNumber() != null
                        && !Objects.equals(processorRequest.getProcessorPhoneNumber(), processor.getProcessorPhoneNumber())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(processorRequest.getProcessorPhoneNumber());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    processor.getUser().setPhoneNo(processorRequest.getProcessorPhoneNumber());
                }

                // Check and update Email
                if (processorRequest.getProcessorEmail() != null
                        && !Objects.equals(processorRequest.getProcessorEmail(), processor.getProcessorEmail())) {
                    Optional<User> userOptional = userRepository.findByEmail(processorRequest.getProcessorEmail());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Email already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    processor.getUser().setEmail(processorRequest.getProcessorEmail());
                }

                // Check and update Processor Category
                if (processorRequest.getProcessorCategoryId() != null) {
                    Optional<ProcessorCategory> processorCategoryOptional = processorCategoryRepository.findByIdAndDeletedFlag(
                            processorRequest.getProcessorCategoryId(), Constants.NO);
                    if (processorCategoryOptional.isEmpty()) {
                        return new ApiResponse<>("ProcessorCategory not found.", null, HttpStatus.NOT_FOUND.value());
                    }
                    ProcessorCategory processorCategory = processorCategoryOptional.get();
                    processor.setProcessorCategory(processorCategory);
                }

                // Check and update Processor SubCategory
                if (processorRequest.getProcessorSubCategoryId() != null) {
                    Optional<ProcessorSubCategory> processorSubCategoryOptional = processorSubCategoryRepository.findByIdAndDeletedFlag(
                            processorRequest.getProcessorSubCategoryId(), Constants.NO);
                    if (processorSubCategoryOptional.isEmpty()) {
                        return new ApiResponse<>("ProcessorSubCategory not found.", null, HttpStatus.NOT_FOUND.value());
                    }
                    ProcessorSubCategory processorSubCategory = processorSubCategoryOptional.get();
                    processor.setProcessorSubCategory(processorSubCategory);
                }

                // Check and update Value Chains
                if (processorRequest.getValueChainIds() != null) {
                    if (processorRequest.getValueChainIds().isEmpty()) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // Replace existing value chains
                    Set<Long> newValueChainIds = new HashSet<>(processorRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = processor.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getProcessorList().remove(processor);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : processorRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!processor.getValueChains().contains(valueChain)) {
                            processor.getValueChains().add(valueChain);
                            valueChain.getProcessorList().add(processor);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                // Update other fields
                updateProcessorFields(processor, processorRequest);
                updateWorkingHours(processor, processorRequest);
                updateBusinessLocation(processor, processorRequest);
                updateWorkingDays(processor, processorRequest);

                // Save the updated processor
                Processor updatedProcessor = processorRepository.save(processor);

                ProcessorResponse processorResponse = modelMapper.map(updatedProcessor, ProcessorResponse.class);

                return new ApiResponse<>("Processor profile successfully updated.",
                        processorResponse, HttpStatus.OK.value());
            }

            return new ApiResponse<>("Processor not found", null, HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            log.info("Error Updating Processor by User id", e);
            return new ApiResponse<>("Error Updating Processor by User ID", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateProcessorFields(Processor processor, ProcessorRequest processorRequest) {
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getProfilePicture(),
                processor::setProfilePicture, processor::getProfilePicture);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getBio(), processor::setBio,
                processor::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getGender(),
                processor::setGender, processor::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getProcessorName(),
                processor::setProcessorName, processor::getProcessorName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getProcessorEmail(),
                processor::setProcessorEmail, processor::getProcessorEmail);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getProcessorDescription(),
                processor::setProcessorDescription, processor::getProcessorDescription);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getProcessorLogo(),
                processor::setProcessorLogo, processor::getProcessorLogo);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getLicenceNumber(),
                processor::setLicenceNumber, processor::getLicenceNumber);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(processorRequest.getProcessorPhoneNumber(),
                processor::setProcessorPhoneNumber, processor::getProcessorPhoneNumber);
    }


    public ApiResponse<?> deleteProcessor(Long processorId) {
        try {
            Optional<Processor> processorOptional = processorRepository.findByUserIdAndDeletedFlag(processorId, Constants.NO);
            if (processorOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();
                Processor processor = processorOptional.get();
                processor.setDeletedFlag(Constants.YES);
                processor.setDeletedAt(LocalDateTime.now());
                processor.setAddedBy(currentUser);

                User user = processor.getUser();
                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedBy(currentUser);
                user.setDeletedFlag(Constants.YES);
                user.setActive(false);

                userRepository.save(user);
                processorRepository.save(processor);

                return new ApiResponse<>("Processor was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Processor not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting processor.", e);

            return new ApiResponse<>("An error occurred while deleting processor.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }












}