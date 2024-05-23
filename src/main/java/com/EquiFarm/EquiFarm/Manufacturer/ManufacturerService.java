package com.EquiFarm.EquiFarm.Manufacturer;

import com.EquiFarm.EquiFarm.Driver.DTO.DriverResponse;
import com.EquiFarm.EquiFarm.Driver.Driver;
import com.EquiFarm.EquiFarm.Manufacturer.Categories.Category;
import com.EquiFarm.EquiFarm.Manufacturer.Categories.CategoryRepo;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.SubCategory;
import com.EquiFarm.EquiFarm.Manufacturer.SubCategories.SubCategoryRepo;
import com.EquiFarm.EquiFarm.Manufacturer.DTO.ManufacturerRequest;
import com.EquiFarm.EquiFarm.Manufacturer.DTO.ManufacturerResponse;
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

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ManufacturerService {
    private final ManufacturerRepo manufacturerRepo;
    private final ModelMapper modelMapper;
    private  final UserRepository userRepository;
    private final CategoryRepo categoryRepo;
    private final SubCategoryRepo subCategoryRepo;
    private final ValueChainRepo valueChainRepo;

    public ApiResponse<?> fetchManufacturerProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Manufacturer> manufacturerOptional = manufacturerRepo.findByUserIdAndDeletedFlag(currentUser.getId(), Constants.NO);
            if (manufacturerOptional.isPresent()) {
                Manufacturer manufacturer = manufacturerOptional.get();
                ManufacturerResponse manufacturerResponse = modelMapper.map(manufacturer, ManufacturerResponse.class);
                return new ApiResponse<>("Success fetching Manufacturer's Profile", manufacturerResponse, HttpStatus.FOUND.value());

            } else {
                return new ApiResponse<>("Manufacturer Profile not found", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.info("Error fetching Manufacturer's Profile", e);
            return new ApiResponse<>("Error fetching Manufacturer's Profile", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getManufacturerByUserId(Long id) {
        try {
            Optional<Manufacturer> manufacturerOptional = manufacturerRepo.findByUserIdAndDeletedFlag(id, Constants.NO);

            if (manufacturerOptional.isPresent()) {
                Manufacturer manufacturer = manufacturerOptional.get();

                System.out.println("Manufacturer Found: " + manufacturer);

                ManufacturerResponse manufacturerUserResponse = modelMapper.map(manufacturer,
                        ManufacturerResponse.class);

                return new ApiResponse<>("Manufacturer fetched successfully", manufacturerUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Manufacturer not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while fetching manufacturer.", e);

            return new ApiResponse<>("An error occurred while fetching Manufacturer.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getALlManufacturers() {
        try {
            List<Manufacturer> manufacturerList = manufacturerRepo.findByDeletedFlag(Constants.NO);

            List<ManufacturerResponse> manufacturerResponse = manufacturerList.stream()
                    .map(manufacturer -> modelMapper.map(manufacturer, ManufacturerResponse.class))
                    .toList();

            return new ApiResponse<>("Manufacturers fetched successfully", manufacturerResponse,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error Occurred Fetching all Manufacturers", e);
            return new ApiResponse<>("Error occurred fetching all manufacturers", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllManufacturersInValueChain(Long valueChainId) {
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()) {
                return new ApiResponse<>("Value chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            List<Manufacturer> manufacturerList = manufacturerRepo.findByValueChainsContaining(valueChain);
            List<ManufacturerResponse> manufacturerResponses = manufacturerList
                    .stream()
                    .map(manufacturer -> modelMapper
                            .map(manufacturer, ManufacturerResponse.class))
                    .toList();
            return new ApiResponse<>("Drivers fetched successfully", manufacturerResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error occurred: ", e);
            return new ApiResponse<>("Error occurred fetching Drivers", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getBySubCategory(SubCategory subCategory){
        try {
            List<Manufacturer> manufacturerList = manufacturerRepo.findBySubCategoryAndDeletedFlag(subCategory, Constants.NO);
            List<ManufacturerResponse> manufacturerResponse = manufacturerList.stream()
                    .map(manufacturer -> modelMapper.map(manufacturer, ManufacturerResponse.class)).toList();
            return new ApiResponse<>("Manufacturers fetched successfully", manufacturerResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error fetching manufacturers by subCategory", e);
            return new ApiResponse<>("Error fetching manufacturers by subCategory", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getByCategory(Category category){
        try {
            List<Manufacturer> manufacturerList = manufacturerRepo.findByCategoryAndDeletedFlag(category, Constants.NO);
            List<ManufacturerResponse> manufacturerResponse = manufacturerList.stream()
                    .map(manufacturer -> modelMapper.map(manufacturer, ManufacturerResponse.class)).toList();
            return new ApiResponse<>("Manufacturers fetched successfully", manufacturerResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error fetching manufacturers by Category", e);
            return new ApiResponse<>("Error fetching manufacturers by Category", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> manufacturerProfileUpdate(ManufacturerRequest manufacturerRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Manufacturer> manufacturerOptional = manufacturerRepo
                    .findByUserIdAndDeletedFlag(currentUser.getId(), Constants.NO);

            if (manufacturerOptional.isPresent()) {
                Manufacturer manufacturer = manufacturerOptional.get();

                if (manufacturerRequest.getIdNumber() != null
                        && manufacturerRequest.getIdNumber().length() > 0
                        && !Objects.equals(manufacturerRequest.getIdNumber(), manufacturer.getIdNumber())) {
                    Optional<Manufacturer> manufacturerIdOptional = manufacturerRepo
                            .findByIdNumber(manufacturerRequest.getIdNumber());
                    if (manufacturerIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null, HttpStatus.FORBIDDEN.value());
                    }
                    manufacturer.setIdNumber(manufacturerRequest.getIdNumber());
                }
                if (manufacturerRequest.getSubCategoryId() != null) {
                    Optional<SubCategory> subCategoryOptional = subCategoryRepo
                            .findByIdAndDeletedFlag(manufacturerRequest.getSubCategoryId(), Constants.NO);
                    if (subCategoryOptional.isEmpty()) {
                        return new ApiResponse<>("Sub Category Not Found", null, HttpStatus.NOT_FOUND.value());
                    }
                    SubCategory subCategory = subCategoryOptional.get();
                    manufacturer.setCategory(subCategory.getCategory());
                    manufacturer.setSubCategory(subCategory);
                }
                if (manufacturerRequest.getValueChainIds() != null) {
                    if (manufacturerRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(manufacturerRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = manufacturer.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getManufacturerList().remove(manufacturer);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : manufacturerRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!manufacturer.getValueChains().contains(valueChain)) {
                            manufacturer.getValueChains().add(valueChain);
                            valueChain.getManufacturerList().add(manufacturer);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }
                manufacturer.setManufacturerVerified(false);
                updateManufacturerFields(manufacturer, manufacturerRequest);
                updateWorkingHours(manufacturer, manufacturerRequest);
                updateBusinessLocation(manufacturer, manufacturerRequest);
                updateWorkingDays(manufacturer, manufacturerRequest);

                Manufacturer updatedManufacturer = manufacturerRepo.save(manufacturer);

                ManufacturerResponse manufacturerResponse = modelMapper.map(updatedManufacturer,
                        ManufacturerResponse.class);

                return new ApiResponse<>("Manufacturer profile was successfully updated.",
                        manufacturerResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Category not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating manufacturer's profile.", e);
            return new ApiResponse<>("An error occurred while updating manufacturer's profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private void updateManufacturerFields(Manufacturer manufacturer,
                                          ManufacturerRequest manufacturerRequest) {
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getUser().getFirstName(),
//                manufacturer.getUser()::setFirstName, manufacturer.getUser()::getFirstName);
//        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getUser().getLastName(),
//                manufacturer.getUser()::setLastName, manufacturer.getUser()::getLastName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getProfilePicture(),
                manufacturer::setProfilePicture, manufacturer::getProfilePicture);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getBio(), manufacturer::setBio,
                manufacturer::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getGender(),
                manufacturer::setGender, manufacturer::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerName(),
                manufacturer::setManufacturerName, manufacturer::getManufacturerName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerEmail(),
                manufacturer::setManufacturerEmail, manufacturer::getManufacturerEmail);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerDescription(),
                manufacturer::setManufacturerDescription, manufacturer::getManufacturerDescription);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerLogo(),
                manufacturer::setManufacturerLogo, manufacturer::getManufacturerLogo);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getLicenceNumber(),
                manufacturer::setLicenceNumber, manufacturer::getLicenceNumber);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerPhoneNumber(),
                manufacturer::setManufacturerPhoneNumber, manufacturer::getManufacturerPhoneNumber);
    }
    private void updateWorkingDays(Manufacturer manufacturer,
                                   ManufacturerRequest manufacturerRequest) {
        if (manufacturerRequest.getWorkingDays() != null) {
            manufacturer.setWorkingDays(manufacturerRequest.getWorkingDays());
        }
    }
    private void updateWorkingHours(Manufacturer manufacturer,
                                    ManufacturerRequest manufacturerRequest) {
        if (manufacturerRequest.getWorkingHours() != null) {
            WorkingHours workingHours = manufacturer.getWorkingHours();
            if (workingHours == null) {
                workingHours = new WorkingHours();
            }
            workingHours.setStartHour(manufacturerRequest.getWorkingHours().getStartHour());
            workingHours.setEndHour(manufacturerRequest.getWorkingHours().getEndHour());
            manufacturer.setWorkingHours(workingHours);
        }
    }
    private void updateBusinessLocation(Manufacturer manufacturer,
                                        ManufacturerRequest manufacturerRequest) {
        if (manufacturerRequest.getBusinessLocation() != null) {
            Cordinates coordinates = manufacturer.getManufacturerLocation();
            if (coordinates == null) {
                coordinates = new Cordinates();
            }
            coordinates.setLatitude(manufacturerRequest.getBusinessLocation().getLatitude());
            coordinates.setLongitude(manufacturerRequest.getBusinessLocation().getLongitude());
            manufacturer.setManufacturerLocation(coordinates);
        }
    }
    public ApiResponse<?> updateManufacturerByManufacturerId(ManufacturerRequest manufacturerRequest, Long manufacturerId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. Unauthenticated User", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Manufacturer> manufacturerOptional = manufacturerRepo.findByIdAndDeletedFlag(manufacturerId, Constants.NO);
            if (manufacturerOptional.isPresent()) {
                Manufacturer manufacturer = manufacturerOptional.get();
                if (manufacturerRequest.getIdNumber() != null
                        && manufacturerRequest.getIdNumber().length() > 0
                        && !Objects.equals(manufacturerRequest.getIdNumber(), manufacturer.getIdNumber())) {
                    Optional<Manufacturer> manufacturerIdOptional = manufacturerRepo
                            .findByIdNumber(manufacturerRequest.getIdNumber());
                    if (manufacturerIdOptional.isPresent()) {
                        return new ApiResponse<>("Id Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    manufacturer.setIdNumber(manufacturerRequest.getIdNumber());
                }

                if (manufacturerRequest.getUser().getPhoneNo() != null
                        && manufacturerRequest.getUser().getPhoneNo().length() > 0
                        && !Objects.equals(manufacturerRequest.getUser().getPhoneNo(), manufacturer.getUser().getPhoneNo())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(manufacturerRequest.getUser().getPhoneNo());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    manufacturer.getUser().setPhoneNo(manufacturerRequest.getUser().getPhoneNo());
                }

                if (manufacturerRequest.getUser().getEmail() != null
                        && manufacturerRequest.getUser().getEmail().length() > 0
                        && !Objects.equals(manufacturerRequest.getUser().getEmail(), manufacturer.getUser().getEmail())) {
                    Optional<User> userOptional = userRepository.findByEmail(manufacturerRequest.getUser().getEmail());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Email already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    manufacturer.getUser().setEmail(manufacturerRequest.getUser().getEmail());
                }

                if (manufacturerRequest.getCategoryId() != null) {
                    Optional<Category> categoryOptional = categoryRepo.findByIdAndDeletedFlag(
                            manufacturerRequest.getCategoryId(), Constants.NO);
                    if (categoryOptional.isEmpty()) {
                        return new ApiResponse<>("Category not found.", null, HttpStatus.NOT_FOUND.value());
                    }
                    Category category = categoryOptional.get();
                    manufacturer.setCategory(category);
                }
                if (manufacturerRequest.getSubCategoryId() != null) {
                    Optional<SubCategory> subCategoryOptional = subCategoryRepo.findByIdAndDeletedFlag(
                            manufacturerRequest.getSubCategoryId(), Constants.NO);
                    if (subCategoryOptional.isEmpty()) {
                        return new ApiResponse<>("SUbCategory not found.", null, HttpStatus.NOT_FOUND.value());
                    }
                    SubCategory subCategory = subCategoryOptional.get();
                    manufacturer.setSubCategory(subCategory);
                }
                if (manufacturerRequest.getValueChainIds() != null) {
                    if (manufacturerRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(manufacturerRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = manufacturer.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getManufacturerList().remove(manufacturer);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : manufacturerRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!manufacturer.getValueChains().contains(valueChain)) {
                            manufacturer.getValueChains().add(valueChain);
                            valueChain.getManufacturerList().add(manufacturer);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }


                updateManufacturerFields(manufacturer, manufacturerRequest);
                updateWorkingHours(manufacturer, manufacturerRequest);
                updateBusinessLocation(manufacturer, manufacturerRequest);
                updateWorkingDays(manufacturer, manufacturerRequest);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerPhoneNumber(),
                        manufacturer::setManufacturerPhoneNumber, manufacturer::getManufacturerPhoneNumber);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerVerified(),
                        manufacturer::setManufacturerVerified, manufacturer::getManufacturerVerified);

                Manufacturer updatedManufacturer = manufacturerRepo.save(manufacturer);

                ManufacturerResponse manufacturerResponse = modelMapper.map(updatedManufacturer,
                        ManufacturerResponse.class);

                return new ApiResponse<>("Manufacturer profile successfully updated.",
                        manufacturerResponse, HttpStatus.OK.value());
            }
            return new ApiResponse<>("Manufacturer not found", null, HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            log.info("Error Updating Manufacturer by User id", e);
            return new ApiResponse<>("Error Updating Manufacturer by User ID", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> updateManufacturerByUserId(ManufacturerRequest manufacturerRequest, Long userId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. Unauthenticated User", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Manufacturer> manufacturerOptional = manufacturerRepo.findByUserIdAndDeletedFlag(userId, Constants.NO);
            if (manufacturerOptional.isPresent()) {
                Manufacturer manufacturer = manufacturerOptional.get();
                if (manufacturerRequest.getIdNumber() != null
                        && manufacturerRequest.getIdNumber().length() > 0
                        && !Objects.equals(manufacturerRequest.getIdNumber(), manufacturer.getIdNumber())) {
                    Optional<Manufacturer> manufacturerIdOptional = manufacturerRepo
                            .findByIdNumber(manufacturerRequest.getIdNumber());
                    if (manufacturerIdOptional.isPresent()) {
                        return new ApiResponse<>("Id Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    manufacturer.setIdNumber(manufacturerRequest.getIdNumber());
                }

                if (manufacturerRequest.getUser().getPhoneNo() != null
                        && manufacturerRequest.getUser().getPhoneNo().length() > 0
                        && !Objects.equals(manufacturerRequest.getUser().getPhoneNo(), manufacturer.getUser().getPhoneNo())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(manufacturerRequest.getUser().getPhoneNo());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    manufacturer.getUser().setPhoneNo(manufacturerRequest.getUser().getPhoneNo());
                }

                if (manufacturerRequest.getUser().getEmail() != null
                        && manufacturerRequest.getUser().getEmail().length() > 0
                        && !Objects.equals(manufacturerRequest.getUser().getEmail(), manufacturer.getUser().getEmail())) {
                    Optional<User> userOptional = userRepository.findByEmail(manufacturerRequest.getUser().getEmail());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Email already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    manufacturer.getUser().setEmail(manufacturerRequest.getUser().getEmail());
                }

                if (manufacturerRequest.getCategoryId() != null) {
                    Optional<Category> categoryOptional = categoryRepo.findByIdAndDeletedFlag(
                            manufacturerRequest.getCategoryId(), Constants.NO);
                    if (categoryOptional.isEmpty()) {
                        return new ApiResponse<>("Category not found.", null, HttpStatus.NOT_FOUND.value());
                    }
                    Category category = categoryOptional.get();
                    manufacturer.setCategory(category);
                }
                if (manufacturerRequest.getSubCategoryId() != null) {
                    Optional<SubCategory> subCategoryOptional = subCategoryRepo.findByIdAndDeletedFlag(
                            manufacturerRequest.getSubCategoryId(), Constants.NO);
                    if (subCategoryOptional.isEmpty()) {
                        return new ApiResponse<>("SUbCategory not found.", null, HttpStatus.NOT_FOUND.value());
                    }
                    SubCategory subCategory = subCategoryOptional.get();
                    manufacturer.setSubCategory(subCategory);
                }
                if (manufacturerRequest.getValueChainIds() != null) {
                    if (manufacturerRequest.getValueChainIds().size() == 0) {
                        return new ApiResponse<>("Value chain is required", null, HttpStatus.BAD_REQUEST.value());
                    }
                    // replace existing value chain
                    Set<Long> newValueChainIds = new HashSet<>(manufacturerRequest.getValueChainIds());

                    Iterator<ValueChain> iterator = manufacturer.getValueChains().iterator();
                    while (iterator.hasNext()) {
                        ValueChain existingValueChain = iterator.next();
                        if (!newValueChainIds.contains(existingValueChain.getId())) {
                            iterator.remove();
                            existingValueChain.getManufacturerList().remove(manufacturer);
                            valueChainRepo.save(existingValueChain);
                        }
                    }
                    for (Long valueId : manufacturerRequest.getValueChainIds()) {
                        Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueId, Constants.NO);
                        if (valueChainOptional.isEmpty()) {
                            return new ApiResponse<>("Value chain with id " + valueId + " does not exist", null, HttpStatus.NOT_FOUND.value());
                        }
                        ValueChain valueChain = valueChainOptional.get();
                        if (!manufacturer.getValueChains().contains(valueChain)) {
                            manufacturer.getValueChains().add(valueChain);
                            valueChain.getManufacturerList().add(manufacturer);
                            valueChainRepo.save(valueChain);
                        }
                    }
                }

                updateManufacturerFields(manufacturer, manufacturerRequest);
                updateWorkingHours(manufacturer, manufacturerRequest);
                updateBusinessLocation(manufacturer, manufacturerRequest);
                updateWorkingDays(manufacturer, manufacturerRequest);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerPhoneNumber(),
                        manufacturer::setManufacturerPhoneNumber, manufacturer::getManufacturerPhoneNumber);

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(manufacturerRequest.getManufacturerVerified(),
                        manufacturer::setManufacturerVerified, manufacturer::getManufacturerVerified);

                Manufacturer updatedManufacturer = manufacturerRepo.save(manufacturer);

                ManufacturerResponse manufacturerResponse = modelMapper.map(updatedManufacturer,
                        ManufacturerResponse.class);

                return new ApiResponse<>("Manufacturer profile successfully updated.",
                        manufacturerResponse, HttpStatus.OK.value());
            }
            return new ApiResponse<>("Manufacturer not found", null, HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            log.info("Error Updating Manufacturer by User id", e);
            return new ApiResponse<>("Error Updating Manufacturer by User ID", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> deleteManufacturer(Long manufacturerId) {
        try {
            Optional<Manufacturer> manufacturerOptional = manufacturerRepo.findByUserIdAndDeletedFlag(manufacturerId, Constants.NO);
            if (manufacturerOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();
                Manufacturer manufacturer = manufacturerOptional.get();
                manufacturer.setDeletedFlag(Constants.YES);
                manufacturer.setDeletedAt(LocalDateTime.now());
                manufacturer.setAddedBy(currentUser);

                User user = manufacturer.getUser();
                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedBy(currentUser);
                user.setDeletedFlag(Constants.YES);
                user.setActive(false);

                userRepository.save(user);
                manufacturerRepo.save(manufacturer);

                return new ApiResponse<>("Manufacturer was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Manufacturer not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting manufacturer.", e);

            return new ApiResponse<>("An error occurred while deleting manufacturer.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


}
