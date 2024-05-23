package com.EquiFarm.EquiFarm.Rating.ServiceProviderRating;

import com.EquiFarm.EquiFarm.Rating.ServiceProviderRating.DTO.ServiceProviderRatingRequest;
import com.EquiFarm.EquiFarm.Rating.ServiceProviderRating.DTO.ServiceProviderRatingResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProviderRepository;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.user.UserRepository;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
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
public class ServiceProviderRatingService {
    private final ServiceProviderRatingRepository serviceProviderRatingRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> createServiceProviderRating(ServiceProviderRatingRequest serviceProviderRatingRequest){
        try{
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null){
                return new ApiResponse <>("Access denied.User is not authenticated.",null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            if (serviceProviderRatingRequest.getStars() < 1 || serviceProviderRatingRequest.getStars() > 5) {
                return new ApiResponse<>("Rating value must be between 1 and 5.",null,
                        HttpStatus.BAD_REQUEST.value());
            }

            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByDeletedFlagAndId(
                    Constants.NO, serviceProviderRatingRequest.getServiceProviderId());
            ServiceProvider serviceProvider = serviceProviderOptional.orElse(null);
            if (serviceProvider == null){
                return new ApiResponse<>("Service provider not found",null,HttpStatus.NOT_FOUND.value());
            }

            ServiceProviderRating serviceProviderRating = new ServiceProviderRating();
            serviceProviderRating.setUser(currentUser);
            serviceProviderRating.setServiceProvider(serviceProvider);
            serviceProviderRating.setStars(serviceProviderRatingRequest.getStars());
            serviceProviderRating.setComment(serviceProviderRatingRequest.getComment());

            serviceProviderRatingRepository.save(serviceProviderRating);

            ServiceProviderRatingResponse serviceProviderRatingResponse = modelMapper.map(serviceProviderRating,
                    ServiceProviderRatingResponse.class);

            return new ApiResponse<ServiceProviderRatingResponse>("Service provider rating submitted successfully.",
                    serviceProviderRatingResponse, HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("An error occurred while adding rating.", e);

            return new ApiResponse<>("An error occurred while adding rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllServiceProviderRating() {
        try {
            List<ServiceProviderRating> serviceProviderRatingList = serviceProviderRatingRepository
                    .findByDeletedFlag(Constants.NO);

            List<ServiceProviderRatingResponse> serviceProviderRatingResponses = serviceProviderRatingList
                    .stream().map(serviceProviderRating -> modelMapper.map(serviceProviderRating,
                            ServiceProviderRatingResponse.class)).collect(Collectors.toList());

            return new ApiResponse<>("Service provider rating fetched successfully",
                    serviceProviderRatingResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching service provider rating.", e);

            return new ApiResponse<>("An error occurred while fetching service provider rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @Transactional
    public ApiResponse<?> getServiceProviderRatingByServiceProviderId(Long serviceProviderId) {
        try {
            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByDeletedFlagAndId(
                    Constants.NO, serviceProviderId);

            if (serviceProviderOptional.isPresent()) {
                ServiceProvider serviceProvider = serviceProviderOptional.get();

                List<ServiceProviderRating> serviceProviderRatingList = serviceProviderRatingRepository
                        .findByDeletedFlagAndServiceProviderId(Constants.NO, serviceProvider.getId());

                List<ServiceProviderRatingResponse> serviceProviderRatingResponses = serviceProviderRatingList
                        .stream().map(serviceProviderRating -> modelMapper.map(serviceProviderRating,
                                ServiceProviderRatingResponse.class)).collect(Collectors.toList());

                return new ApiResponse<>("Service provider rating fetched successfully",
                        serviceProviderRatingResponses, HttpStatus.OK.value());
            }
            else {
                return new ApiResponse<>("Service provider not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching service provider rating.", e);

            return new ApiResponse<>("An error occurred while fetching service provider rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getServiceProviderRatingByUserId(Long userId) {
        try {

            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(
                    Constants.NO, userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                List<ServiceProviderRating> serviceProviderRatingList = serviceProviderRatingRepository
                        .findByDeletedFlagAndUserId(Constants.NO, user.getId());

                List<ServiceProviderRatingResponse> serviceProviderRatingResponses = serviceProviderRatingList
                        .stream().map(serviceProviderRating -> modelMapper.map(serviceProviderRating,
                                ServiceProviderRatingResponse.class)).collect(Collectors.toList());

                return new ApiResponse<>("Service provider rating fetched successfully",
                        serviceProviderRatingResponses, HttpStatus.OK.value());
            }
            else {
                return new ApiResponse<>("User not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching service provider rating.", e);

            return new ApiResponse<>("An error occurred while fetching service provider rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> deleteServiceProviderRating(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied.User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<ServiceProviderRating> serviceProviderRatingOptional = serviceProviderRatingRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (serviceProviderRatingOptional.isPresent()) {

                ServiceProviderRating serviceProviderRating = serviceProviderRatingOptional.get();
                serviceProviderRating.setDeletedFlag(Constants.YES);
                serviceProviderRating.setDeletedAt(LocalDateTime.now());
                serviceProviderRating.setAddedBy(currentUser);

                serviceProviderRatingRepository.save(serviceProviderRating);

                return new ApiResponse<>("Service provider rating was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Service provider rating not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting service provider rating.", e);

            return new ApiResponse<>("An error occurred while deleting service provider rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
