package com.EquiFarm.EquiFarm.Staff;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusiness;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.DTO.AgriBusinessUserResponse;
import com.EquiFarm.EquiFarm.Driver.DTO.DriverResponse;
import com.EquiFarm.EquiFarm.Driver.Driver;
import com.EquiFarm.EquiFarm.Driver.DriverRepository;
import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.DTO.FarmProductsResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.EquiFarm.EquiFarm.Manufacturer.DTO.ManufacturerResponse;
import com.EquiFarm.EquiFarm.Manufacturer.Manufacturer;
import com.EquiFarm.EquiFarm.Manufacturer.ManufacturerRepo;
import com.EquiFarm.EquiFarm.ServiceProvider.DTO.ServiceProviderUserResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProviderRepository;
import com.EquiFarm.EquiFarm.Staff.DTO.StaffUserRequest;
import com.EquiFarm.EquiFarm.Staff.DTO.StaffUserResponse;
import com.EquiFarm.EquiFarm.Warehouse.DTO.WarehouseResponse;
import com.EquiFarm.EquiFarm.Warehouse.Warehouse;
import com.EquiFarm.EquiFarm.Warehouse.WarehouseRepo;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffService {
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final FarmProductsRepository farmProductsRepository;
    private final FarmerRepository farmerRepository;
    private final DriverRepository driverRepository;
    private final AgriBusinessRepository agriBusinessRepository;
    private final ManufacturerRepo manufacturerRepo;
    private final ServiceProviderRepository serviceProviderRepository;
    private final WarehouseRepo warehouseRepo;
    private final ModelMapper modelMapper;
    public ApiResponse<?> getStaffProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Staff> staffOptional = staffRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

//            log.info("Staff Profile: {}", staffOptional.get().getUser().getEmail());

            if(staffOptional.isPresent()) {
                Staff staff = staffOptional.get();
                StaffUserResponse staffUserResponse = modelMapper.map(staff, StaffUserResponse.class);

                return new ApiResponse<>("Staff profile fetched successfully.", staffUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Staff profile not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmer profile.", e);

            return new ApiResponse<>("An error occurred while fetching staff profile.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getStaffById(Long id) {
        try {
            Optional<Staff> staffOptional = staffRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (staffOptional.isPresent()) {
                Staff staff = staffOptional.get();
                StaffUserResponse staffUserResponse = modelMapper.map(staff,StaffUserResponse.class);

                return new ApiResponse<>("Staff Fetched Successfully.", staffUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Staff not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching staff.", e);

            return new ApiResponse<>("An error occurred while fetching staff.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getAllStaff() {
        try {
            List<Staff> staffList = staffRepository.findByDeletedFlag(Constants.NO);
            List<StaffUserResponse> staffUserResponses = staffList.stream()
                    .map(staff -> modelMapper.map(staff, StaffUserResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Staff fetched successfully", staffUserResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching staff.", e);

            return new ApiResponse<>("An error occurred while fetching staff.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> staffProfileUpdate(StaffUserRequest staffUserRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Staff> staffOptional = staffRepository.findByDeletedFlagAndId(Constants.NO,
                    currentUser.getId());

            if (staffOptional.isPresent()) {
                Staff staff = staffOptional.get();

                if (staffUserRequest.getIdNumber() != null && staffUserRequest.getIdNumber().length() >0
                        && !Objects.equals(staffUserRequest.getIdNumber(), staff.getIdNumber())) {

                    Optional<Staff> staffIdOptional = staffRepository
                            .findByIdNumber(staffUserRequest.getIdNumber());

                    if (staffIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    staff.setIdNumber(staffUserRequest.getIdNumber());
                }

                if (staffUserRequest.getUser().getPhoneNo() != null
                        && staffUserRequest.getUser().getPhoneNo().length() > 0
                        && !Objects.equals(staffUserRequest.getUser().getPhoneNo(), staff.getUser().getPhoneNo())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(staffUserRequest.getUser().getPhoneNo());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null,
                                HttpStatus.NOT_MODIFIED.value());
                    }
                    staff.getUser().setPhoneNo(staffUserRequest.getUser().getPhoneNo());
                }

                if (staffUserRequest.getUser().getEmail() != null
                        && staffUserRequest.getUser().getEmail().length() > 0
                        && !Objects.equals(staffUserRequest.getUser().getEmail(), staff.getUser().getEmail())) {
                    Optional<User> userOptional = userRepository.findByEmail(staffUserRequest.getUser().getEmail());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Email already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    staff.getUser().setEmail(staffUserRequest.getUser().getEmail());
                }
                updateStaffFields(staff, staffUserRequest);

                Staff updatedStaff = staffRepository.save(staff);
                StaffUserResponse staffUserResponse = modelMapper.map(updatedStaff, StaffUserResponse.class);

                return new ApiResponse<>("Staff profile was successfully updated.", staffUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Staff not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while updating the staff.", e);

            return new ApiResponse<>("An error occurred while updating the staff.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> staffProfileUpdateByUserId( Long userId, StaffUserRequest staffUserRequest) {
        try {
            Optional<Staff> staffOptional = staffRepository.findByDeletedFlagAndUserId(Constants.NO,
                    userId);

            if (staffOptional.isPresent()) {
                Staff staff = staffOptional.get();

                if (staffUserRequest.getIdNumber() != null && staffUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(staffUserRequest.getIdNumber(), staff.getIdNumber())) {

                    Optional<Staff> staffIdOptional = staffRepository
                            .findByIdNumber(staffUserRequest.getIdNumber());

                    if (staffIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    staff.setIdNumber(staffUserRequest.getIdNumber());
                }

                if (staffUserRequest.getUser().getPhoneNo() != null
                        && staffUserRequest.getUser().getPhoneNo().length() > 0
                        && !Objects.equals(staffUserRequest.getUser().getPhoneNo(), staff.getUser().getPhoneNo())) {
                    Optional<User> userOptional = userRepository.findUserByPhoneNo(staffUserRequest.getUser().getPhoneNo());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Phone Number already exists.", null,
                                HttpStatus.NOT_MODIFIED.value());
                    }
                    staff.getUser().setPhoneNo(staffUserRequest.getUser().getPhoneNo());
                }

                if (staffUserRequest.getUser().getEmail() != null
                        && staffUserRequest.getUser().getEmail().length() > 0
                        && !Objects.equals(staffUserRequest.getUser().getEmail(), staff.getUser().getEmail())) {
                    Optional<User> userOptional = userRepository.findByEmail(staffUserRequest.getUser().getEmail());
                    if (userOptional.isPresent()) {
                        return new ApiResponse<>("Email already exists.", null, HttpStatus.NOT_MODIFIED.value());
                    }
                    staff.getUser().setEmail(staffUserRequest.getUser().getEmail());
                }

                //Admin to update role
                if (staffUserRequest.getStaffRole() != null
                        && !Objects.equals(staffUserRequest.getStaffRole(), staff.getStaffRole())) {
                    staff.setStaffRole(staffUserRequest.getStaffRole());
                }

                updateStaffFields(staff, staffUserRequest);

                Staff updatedStaff = staffRepository.save(staff);
                StaffUserResponse staffUserResponse = modelMapper.map(updatedStaff, StaffUserResponse.class);

                return new ApiResponse<>("Staff profile was successfully updated.", staffUserResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Staff not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while updating the staff.", e);

            return new ApiResponse<>("An error occurred while updating the staff.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private void updateStaffFields(Staff staff, StaffUserRequest staffUserRequest) {
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(staffUserRequest.getUser().getFirstName(),
                staff.getUser()::setFirstName, staff.getUser()::getFirstName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(staffUserRequest.getUser().getLastName(),
                staff.getUser()::setLastName, staff.getUser()::getLastName);

        FieldUpdateUtil.updateFieldIfNotNullAndChanged(staffUserRequest.getBio(), staff::setBio,
                staff::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(staffUserRequest.getGender(),
                staff::setGender, staff::getGender);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(staffUserRequest.getProfilePicture(),
                staff::setProfilePicture, staff::getProfilePicture);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(staffUserRequest.getAvailable(),
                staff::setAvailable, staff::getAvailable);
    }
    public ApiResponse<?> staffDelete(Long id) {
        try {
            Optional<Staff> staffOptional = staffRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (staffOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();

                Staff staff = staffOptional.get();
                staff.setDeletedFlag(Constants.YES);
                staff.setDeletedAt(LocalDateTime.now());
                staff.setDeletedBy(currentUser);

                User user = staff.getUser();
                user.setDeletedAt(LocalDateTime.now());
                user.setDeletedBy(currentUser);
                user.setDeletedFlag(Constants.YES);
                user.setActive(false);

                userRepository.save(user);
                staffRepository.save(staff);

                return new ApiResponse<>("Staff was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Staff not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting staff.", e);

            return new ApiResponse<>("An error occurred while deleting staff.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> verifyFarmProduct(Long farmProductId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<FarmProducts> farmProductOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO,
                    farmProductId);

            if (farmProductOptional.isEmpty()) {
                return new ApiResponse<>("Farm  Not Found", null, HttpStatus.NOT_FOUND.value());
            }

            FarmProducts farmProduct = farmProductOptional.get();
            farmProduct.setIsVerified(true);
            farmProduct.setUpdatedBy(currentUser);

            FarmProducts savedFarmProduct = farmProductsRepository.save(farmProduct);
            FarmProductsResponse farmProductsResponse = modelMapper.map(savedFarmProduct, FarmProductsResponse.class);

            return new ApiResponse<>("Success Verifying the Product", farmProductsResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error verifying farm product", e);

            return new ApiResponse<>("Error verifying farm product", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> verifyFarmer(Long farmerId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndId(Constants.NO,
                    farmerId);

            if (farmerOptional.isEmpty()) {
                return new ApiResponse<>("Farmer  Not Found", null, HttpStatus.NOT_FOUND.value());
            }

            Farmer farmer = farmerOptional.get();
            farmer.setIsVerified(true);
            farmer.setUpdatedBy(currentUser);

            Farmer savedFarmer = farmerRepository.save(farmer);
            FarmersResponse farmersResponse = modelMapper.map(savedFarmer, FarmersResponse.class);

            return new ApiResponse<>("Success Verifying the Farmer", farmersResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error verifying farmer", e);

            return new ApiResponse<>("Error verifying farmer", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> verifyDriver(Long driverId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Driver> driverOptional = driverRepository.findByDeletedFlagAndId(Constants.NO,
                    driverId);

            if (driverOptional.isEmpty()) {
                return new ApiResponse<>("Driver  Not Found", null, HttpStatus.NOT_FOUND.value());
            }

            Driver driver = driverOptional.get();
            driver.setIsVerified(true);
            driver.setUpdatedBy(currentUser);

            Driver savedDriver = driverRepository.save(driver);
            DriverResponse driverResponse = modelMapper.map(savedDriver, DriverResponse.class);

            return new ApiResponse<>("Success Verifying the Driver", driverResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error verifying driver", e);

            return new ApiResponse<>("Error verifying driver", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> verifyAgriBusiness(Long agriBusinessId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndId(Constants.NO,
                    agriBusinessId);

            if (agriBusinessOptional.isEmpty()) {
                return new ApiResponse<>("AgriBusiness  Not Found", null, HttpStatus.NOT_FOUND.value());
            }

            AgriBusiness agriBusiness = agriBusinessOptional.get();
            agriBusiness.setBusinessVerified(true);
            agriBusiness.setUpdatedBy(currentUser);

            AgriBusiness savedAgriBusiness = agriBusinessRepository.save(agriBusiness);
            AgriBusinessUserResponse agriBusinessUserResponse = modelMapper.map(savedAgriBusiness, AgriBusinessUserResponse.class);

            return new ApiResponse<>("Success Verifying the AgriBusiness", agriBusinessUserResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error verifying agriBusiness", e);

            return new ApiResponse<>("Error verifying agriBusiness", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> verifyManufacturer(Long manufacturerId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Manufacturer> manufacturerOptional = manufacturerRepo.findByIdAndDeletedFlag(manufacturerId,
                    Constants.NO);

            if (manufacturerOptional.isEmpty()) {
                return new ApiResponse<>("Manufacturer Not Found", null, HttpStatus.NOT_FOUND.value());
            }

            Manufacturer manufacturer = manufacturerOptional.get();
            manufacturer.setManufacturerVerified(true);
            manufacturer.setUpdatedBy(currentUser);

            Manufacturer savedManufacturer= manufacturerRepo.save(manufacturer);
            ManufacturerResponse manufacturerResponse = modelMapper.map(savedManufacturer, ManufacturerResponse.class);

            return new ApiResponse<>("Success Verifying the Manufacturer", manufacturerResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error verifying manufacturer", e);

            return new ApiResponse<>("Error verifying manufacturer", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> verifyServiceProvider(Long serviceProviderId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByDeletedFlagAndId(Constants.NO,
                    serviceProviderId);

            if (serviceProviderOptional.isEmpty()) {
                return new ApiResponse<>("AgriBusiness  Not Found", null, HttpStatus.NOT_FOUND.value());
            }

            ServiceProvider serviceProvider = serviceProviderOptional.get();
            serviceProvider.setBusinessVerified(true);
            serviceProvider.setUpdatedBy(currentUser);

            ServiceProvider savedServiceProvider = serviceProviderRepository.save(serviceProvider);
            ServiceProviderUserResponse serviceProviderUserResponse = modelMapper.map(savedServiceProvider, ServiceProviderUserResponse.class);

            return new ApiResponse<>("Success Verifying the ServiceProvider", serviceProviderUserResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error verifying serviceProvider", e);

            return new ApiResponse<>("Error verifying serviceProvider", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> verifyWarehouse(Long warehouseId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access Denied. User Unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Warehouse> warehouseOptional = warehouseRepo.findByIdAndDeletedFlag(warehouseId,
                    Constants.NO);

            if (warehouseOptional.isEmpty()) {
                return new ApiResponse<>("Warehouse Not Found", null, HttpStatus.NOT_FOUND.value());
            }

            Warehouse warehouse = warehouseOptional.get();
            warehouse.setWarehouseVerified(true);
            warehouse.setUpdatedBy(currentUser);

            Warehouse savedWarehouse = warehouseRepo.save(warehouse);
            WarehouseResponse warehouseResponse = modelMapper.map(savedWarehouse, WarehouseResponse.class);

            return new ApiResponse<>("Success Verifying the Warehouse", warehouseResponse,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error verifying warehouse", e);

            return new ApiResponse<>("Error verifying warehouse", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
