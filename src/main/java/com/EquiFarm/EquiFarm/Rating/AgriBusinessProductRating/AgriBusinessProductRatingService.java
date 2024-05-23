package com.EquiFarm.EquiFarm.Rating.AgriBusinessProductRating;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessRepository;
import com.EquiFarm.EquiFarm.Rating.AgriBusinessProductRating.DTO.AgriBusinessProductRatingRequest;
import com.EquiFarm.EquiFarm.Rating.AgriBusinessProductRating.DTO.AgriBusinessProductRatingResponse;
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

public class AgriBusinessProductRatingService {
    private final AgriBusinessProductRatingRepository agriBusinessProductRatingRepository;
    private final AgriBusinessProductRepository agriBusinessProductRepository;
    private final AgriBusinessRepository agriBusinessRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public ApiResponse<?> createAgriBusinessProductRating(AgriBusinessProductRatingRequest agriBusinessProductRatingRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            System.out.println(agriBusinessProductRatingRequest.getStars());

            if (agriBusinessProductRatingRequest.getStars() < 1 || agriBusinessProductRatingRequest.getStars() > 5) {
                return new ApiResponse<>("Rating value must be between 1 and 5.", null,
                        HttpStatus.BAD_REQUEST.value());
            }

            Optional<AgriBusinessProduct> agriBusinessProductOptional = agriBusinessProductRepository
                    .findByDeletedFlagAndId(Constants.NO, agriBusinessProductRatingRequest.getAgriBusinessProductId());
            AgriBusinessProduct agriBusinessProduct = agriBusinessProductOptional.orElse(null);
            if (agriBusinessProduct == null) {
                return new ApiResponse<>("AgriBusinessProduct not found", null,
                        HttpStatus.NOT_FOUND.value());
            }

            AgriBusinessProductRating agriBusinessProductRating = new AgriBusinessProductRating();
            agriBusinessProductRating.setUser(currentUser);
            agriBusinessProductRating.setProduct(agriBusinessProduct);
            agriBusinessProductRating.setStars(agriBusinessProductRatingRequest.getStars());
            agriBusinessProductRating.setComment(agriBusinessProductRatingRequest.getComment());

            // Save the rating to the database
            agriBusinessProductRatingRepository.save(agriBusinessProductRating);

            AgriBusinessProductRatingResponse agriBusinessProductRatingResponse = modelMapper
                    .map(agriBusinessProductRating, AgriBusinessProductRatingResponse.class);

            return new ApiResponse<AgriBusinessProductRatingResponse>("AgriBusinessProduct rating submitted successfully.",
                    agriBusinessProductRatingResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {
            // Handling exceptions
            log.error("An error occurred while adding AgriBusinessProduct rating.", e);
            return new ApiResponse<>("An error occurred while adding AgriBusinessProduct rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getAllAgriBusinessProductRatings() {
        try {
            List<AgriBusinessProductRating> agriBusinessProductRatingList = agriBusinessProductRatingRepository
                    .findByDeletedFlag(Constants.NO);

            List<AgriBusinessProductRatingResponse> agriBusinessProductRatingResponses = agriBusinessProductRatingList
                    .stream()
                    .map(agriBusinessProductRating -> modelMapper.map(agriBusinessProductRating, AgriBusinessProductRatingResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("AgriBusinessProduct ratings fetched successfully", agriBusinessProductRatingResponses,
                    HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("An error occurred while fetching AgriBusinessProduct ratings.", e);
            return new ApiResponse<>("An error occurred while fetching AgriBusinessProduct ratings.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getAgriBusinessProductRatingByAgriBusinessProductId(Long id) {
        try {
            Optional<AgriBusinessProductRating> agriBusinessProductRatingOptional = agriBusinessProductRatingRepository
                    .findByDeletedFlagAndId(Constants.NO, id);

            if (agriBusinessProductRatingOptional.isPresent()) {
                AgriBusinessProductRating agriBusinessProductRating = agriBusinessProductRatingOptional.get();

                AgriBusinessProductRatingResponse agriBusinessProductRatingResponse = modelMapper.map(agriBusinessProductRating, AgriBusinessProductRatingResponse.class);

                return new ApiResponse<>("AgriBusinessProduct rating fetched successfully.", agriBusinessProductRatingResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("AgriBusinessProduct rating not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching AgriBusinessProduct rating.", e);

            return new ApiResponse<>("An error occurred while fetching AgriBusinessProduct rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getAgriBusinessProductRatingsByUserId(Long userId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                List<AgriBusinessProductRating> agriBusinessProductRatingsByUser = agriBusinessProductRatingRepository.findByUser(user);

                List<AgriBusinessProductRatingResponse> agriBusinessProductRatingResponseList = agriBusinessProductRatingsByUser.stream()
                        .map(agriBusinessProductRating -> modelMapper.map(agriBusinessProductRating, AgriBusinessProductRatingResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("AgriBusinessProduct ratings fetched successfully.", agriBusinessProductRatingResponseList,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching AgriBusinessProduct ratings.", e);
            return new ApiResponse<>("An error occurred while fetching AgriBusinessProduct ratings.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteAgriBusinessProductRating(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<AgriBusinessProductRating> agriBusinessProductRatingOptional = agriBusinessProductRatingRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (agriBusinessProductRatingOptional.isPresent()) {

                AgriBusinessProductRating agriBusinessProductRating = agriBusinessProductRatingOptional.get();
                agriBusinessProductRating.setDeletedFlag(Constants.YES);
                agriBusinessProductRating.setDeletedAt(LocalDateTime.now());
                agriBusinessProductRating.setAddedBy(currentUser);

                agriBusinessProductRatingRepository.save(agriBusinessProductRating);

                return new ApiResponse<>("AgriBusinessProduct rating was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("AgriBusinessProduct rating not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting AgriBusinessProduct rating.", e);

            return new ApiResponse<>("An error occurred while deleting AgriBusinessProduct rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }








}
