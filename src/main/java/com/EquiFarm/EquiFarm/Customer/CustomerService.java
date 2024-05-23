package com.EquiFarm.EquiFarm.Customer;

import org.apache.tomcat.util.bcel.Const;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Customer.DTO.CustomerRequest;
import com.EquiFarm.EquiFarm.Customer.DTO.CustomerResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;

    @Transactional
    public ApiResponse<?> getCustomerProfile() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<Customer> customerOptional = customerRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();

                CustomerResponse customerResponse = modelMapper.map(customer, CustomerResponse.class);

                return new ApiResponse<>("Customer profile fetched successfully.", customerResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Customer profile not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching customer profile.", e);
            return new ApiResponse<>("An error occurred while fetching customer profile", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getCustomerByProfileId(Long id) {
        try {
            Optional<Customer> customerOptional = customerRepository.findByDeletedFlagAndId(Constants.NO, id);
            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();

                CustomerResponse customerResponse = modelMapper.map(customer, CustomerResponse.class);

                return new ApiResponse<>("Customer fetched successfully.", customerResponse, HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Customer not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while fetching customer.", e);

            return new ApiResponse<>("An error occurred while fetching customer.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllCustomers() {
        try {
            List<Customer> customerList = customerRepository.findByDeletedFlag(Constants.NO);

            List<CustomerResponse> customerResponseList = customerList.stream()
                    .map(customer -> modelMapper.map(customer, CustomerResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Customers fetched successfully.", customerResponseList, HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching customers.", e);

            return new ApiResponse<>("An error occurred while fetching customers.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> customerProfileUpdate(CustomerRequest customerRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Customer> customerOptional = customerRepository.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());

            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();
                User user = customer.getUser();

                if (customerRequest.getIdNumber() != null
                        && customerRequest.getIdNumber().length() > 0
                        && !Objects.equals(customerRequest.getIdNumber(), customer.getIdNumber())) {
                    Optional<Customer> customerIdOptional = customerRepository
                            .findByIdNumber(customerRequest.getIdNumber());
                    if (customerIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    customer.setIdNumber(customerRequest.getIdNumber());
                }

                updateCustomerFields(customerRequest, customer);
                userRepository.save(user);
                Customer updatedCustomer = customerRepository.save(customer);

                CustomerResponse customerResponse = modelMapper.map(updatedCustomer, CustomerResponse.class);
                return new ApiResponse<>("Customer profile updated successfully.", customerResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Customer not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating profile", e);
            return new ApiResponse<>("An error occurred while updating profile", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> updateCustomerProfile(CustomerRequest customerRequest, Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<Customer> customerOptional = customerRepository.findByDeletedFlagAndId(Constants.NO,
                    id);

            if (customerOptional.isPresent()) {
                Customer customer = customerOptional.get();
                User user = customer.getUser();

                if (customerRequest.getIdNumber() != null
                        && customerRequest.getIdNumber().length() > 0
                        && !Objects.equals(customerRequest.getIdNumber(), customer.getIdNumber())) {
                    Optional<Customer> customerIdOptional = customerRepository
                            .findByIdNumber(customerRequest.getIdNumber());
                    if (customerIdOptional.isPresent()) {
                        return new ApiResponse<>("User with this Id Number already exists.", null,
                                HttpStatus.NOT_FOUND.value());
                    }
                    customer.setIdNumber(customerRequest.getIdNumber());
                }

                FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getEmail(), customer.getUser()::setEmail,
                        customer.getUser()::getEmail);
                FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getPhoneNo(),
                        customer.getUser()::setPhoneNo,
                        customer.getUser()::getPhoneNo);
                        

                updateCustomerFields(customerRequest, customer);
                userRepository.save(user);
                Customer updatedCustomer = customerRepository.save(customer);

                CustomerResponse customerResponse = modelMapper.map(updatedCustomer, CustomerResponse.class);
                return new ApiResponse<>("Customer profile updated successfully.", customerResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Customer not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while updating profile", e);
            return new ApiResponse<>("An error occurred while updating profile", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void updateCustomerFields(CustomerRequest customerRequest, Customer customer) {
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getFirstName(), customer.getUser()::setFirstName,
                customer.getUser()::getFirstName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getLastName(), customer.getUser()::setLastName,
                customer.getUser()::getLastName);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getBio(), customer::setBio, customer::getBio);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getProfilePicture(), customer::setProfilePicture,
                customer::getProfilePicture);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getLatitude(), customer::setLatitude,
                customer::getLatitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getLongitude(), customer::setLongitude,
                customer::getLongitude);
        FieldUpdateUtil.updateFieldIfNotNullAndChanged(customerRequest.getGender(), customer::setGender,
                customer::getGender);
    }
    @Transactional
    public ApiResponse<?> customerDelete(Long id) {
        try {
            Optional<Customer> customerOptional = customerRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (customerOptional.isPresent()) {
                User currentUser = SecurityUtils.getCurrentUser();

                Customer customer = customerOptional.get();
                customer.setDeletedFlag(Constants.YES);
                customer.setDeletedAt(LocalDateTime.now());
                customer.setDeletedBy(currentUser);

                User user = customer.getUser();
                user.setDeletedFlag(Constants.YES);
                user.setDeletedBy(currentUser);
                user.setDeletedAt(LocalDateTime.now());

                user.setActive(false);
                User deletedUser = userRepository.save(user);
                Customer deletedCustomer = customerRepository.save(customer);

                return new ApiResponse<>("Customer was successfully deleted.", null, HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Customer not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while deleting customer", e);

            return new ApiResponse<>("An error occurred while deleting customer", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
