package com.EquiFarm.EquiFarm.Admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Admin.DTO.AdminResponse;
import com.EquiFarm.EquiFarm.Admin.DTO.AdminUserRequest;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;

import jakarta.transaction.Transactional;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> getAdminProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Admin> adminOptional = adminRepository.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();

                AdminResponse adminResponse = modelMapper.map(admin, AdminResponse.class);

                return new ApiResponse<AdminResponse>("Admin profile fetched successfully.", adminResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Admin profile not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching admin profile.", e);
            return new ApiResponse<>("An error occurred while fetching admin profile", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAdminByAdminId(Long id) {
        try {
            Optional<Admin> adminOptional = adminRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                AdminResponse adminResponse = modelMapper.map(admin, AdminResponse.class);

                return new ApiResponse<AdminResponse>("Admin Fetched Successfully.", adminResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Admin not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching admin.", e);

            return new ApiResponse<>("An error occurred while fetching admin.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllAdmins() {
        try {
            List<Admin> adminList = adminRepository.findByDeletedFlag(Constants.NO);

            List<AdminResponse> adminResponseList = adminList.stream()
                    .map(admin -> modelMapper.map(admin, AdminResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Admins fetched successfully.", adminResponseList, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching admins.", e);

            return new ApiResponse<>("An error occurred while fetching admins.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> adminProfileUpdate(AdminUserRequest adminUserRequest) {

        try {
            User currentUser = SecurityUtils.getCurrentUser();
            Optional<Admin> adminOptional = adminRepository.findByDeletedFlagAndId(Constants.NO, currentUser.getId());

            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();

                // Update admin idNumber, Bio and profilePicture
                if (adminUserRequest.getIdNumber() != null && adminUserRequest.getIdNumber().length() > 0
                        && !Objects.equals(admin.getIdNumber(), adminUserRequest.getIdNumber())) {
                    Optional<Admin> adminIdOptional = adminRepository.findByIdNumber(adminUserRequest.getIdNumber());
                    if (adminIdOptional.isPresent()) {
                        return new ApiResponse<>("User with the Id Number already exists.", null,
                                HttpStatus.BAD_REQUEST.value());
                    }
                    admin.setIdNumber(adminUserRequest.getIdNumber());
                }
                if (adminUserRequest.getBio() != null && adminUserRequest.getBio().length() > 0
                        && !Objects.equals(admin.getBio(), adminUserRequest.getIdNumber())) {
                    admin.setBio(adminUserRequest.getBio());
                }
                if (adminUserRequest.getProfilePicture() != null && adminUserRequest.getProfilePicture().length() > 0
                        && !Objects.equals(admin.getProfilePicture(), adminUserRequest.getProfilePicture())) {
                    admin.setProfilePicture(adminUserRequest.getProfilePicture());
                }

                // Update User firstName and LastName
                User user = admin.getUser();
                if (adminUserRequest.getFirstName() != null && adminUserRequest.getFirstName().length() > 0
                        && !Objects.equals(user.getFirstName(), adminUserRequest.getFirstName())) {
                    user.setFirstName(adminUserRequest.getFirstName());
                }
                if (adminUserRequest.getLastName() != null && adminUserRequest.getLastName().length() > 0
                        && !Objects.equals(user.getLastName(), adminUserRequest.getLastName())) {
                    user.setLastName(adminUserRequest.getLastName());
                }

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(adminUserRequest.getGender(),
                        admin::setGender,
                        admin::getGender);

                User updatedUser = userRepository.save(user);

                Admin updatedAdmin = adminRepository.save(admin);
                AdminResponse adminResponse = modelMapper.map(updatedAdmin, AdminResponse.class);

                return new ApiResponse<>("Admin Updated Successfully", adminResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Admin not found", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating profile", e);

            return new ApiResponse<>("An error occurred while updating profile", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> adminDelete(Long id) {

        try {

            Optional<Admin> adminOptional = adminRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (adminOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();

                Admin admin = adminOptional.get();
                admin.setDeletedFlag(Constants.YES);
                admin.setDeletedAt(LocalDateTime.now());
                admin.setDeletedBy(currentUser);

                User user = admin.getUser();
                user.setDeletedFlag(Constants.YES);
                user.setDeletedBy(currentUser);
                user.setDeletedAt(LocalDateTime.now());

                user.setActive(false);
                User deletedUser = userRepository.save(user);
                Admin deletedAdmin = adminRepository.save(admin);

                return new ApiResponse<>("Admin was successfully deleted.", null, HttpStatus.NO_CONTENT.value());
            } else {
                return new ApiResponse<>("Admin not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting admin", e);

            return new ApiResponse<>("An error occurred while deleting admin", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> updateAdminUserId(Long userId, AdminUserRequest adminUserRequest) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return new ApiResponse<>("User Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();
            Optional<Admin> adminOptional = adminRepository.findByUser(user);
            if (adminOptional.isEmpty()) {
                return new ApiResponse<>("Admin Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Admin admin = adminOptional.get();
            if (adminUserRequest.getIdNumber() != null && adminUserRequest.getIdNumber().length() > 0
                    && !Objects.equals(admin.getIdNumber(), adminUserRequest.getIdNumber())) {
                Optional<Admin> adminIdOptional = adminRepository.findByIdNumber(adminUserRequest.getIdNumber());
                if (adminIdOptional.isPresent()) {
                    return new ApiResponse<>("User with the Id Number already exists.", null,
                            HttpStatus.BAD_REQUEST.value());
                }
                admin.setIdNumber(adminUserRequest.getIdNumber());
            }
            if (adminUserRequest.getBio() != null && adminUserRequest.getBio().length() > 0
                    && !Objects.equals(admin.getBio(), adminUserRequest.getIdNumber())) {
                admin.setBio(adminUserRequest.getBio());
            }
            if (adminUserRequest.getProfilePicture() != null && adminUserRequest.getProfilePicture().length() > 0
                    && !Objects.equals(admin.getProfilePicture(), adminUserRequest.getProfilePicture())) {
                admin.setProfilePicture(adminUserRequest.getProfilePicture());
            }

            if (adminUserRequest.getFirstName() != null && adminUserRequest.getFirstName().length() > 0
                    && !Objects.equals(user.getFirstName(), adminUserRequest.getFirstName())) {
                user.setFirstName(adminUserRequest.getFirstName());
            }
            if (adminUserRequest.getLastName() != null && adminUserRequest.getLastName().length() > 0
                    && !Objects.equals(user.getLastName(), adminUserRequest.getLastName())) {
                user.setLastName(adminUserRequest.getLastName());
            }

            FieldUpdateUtil.updateFieldIfNotNullAndChanged(adminUserRequest.getGender(),
                    admin::setGender,
                    admin::getGender);
            userRepository.save(user);
            Admin updatedAdmin = adminRepository.save(admin);
            AdminResponse adminResponse = modelMapper.map(updatedAdmin, AdminResponse.class);

            return new ApiResponse<>("Admin Updated Successfully", adminResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("An error occurred Updating Admin: ", e);
            return new ApiResponse<>("An error occurred Updating Admin", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> fetchAdminByUserId(Long userId) {
        try {
            Optional<Admin> adminOptional = adminRepository.findByDeletedFlagAndUserId(Constants.NO, userId);
            if (adminOptional.isEmpty()) {
                return new ApiResponse<>("Admin not Found", null, HttpStatus.NOT_FOUND.value());
            }
            Admin admin = adminOptional.get();
            AdminResponse adminResponse = modelMapper.map(admin, AdminResponse.class);
            return new ApiResponse<>("Success fetching admin by id", adminResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error fetching admin by userId: ", e);
            return new ApiResponse<>("Error fetching admin by User Id", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
