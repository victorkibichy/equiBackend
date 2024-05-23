package com.EquiFarm.EquiFarm.Rating.AgriBusinessRating;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusiness;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessRepository;
import com.EquiFarm.EquiFarm.Rating.AgriBusinessRating.DTO.AgriBusinessRatingRequest;
import com.EquiFarm.EquiFarm.Rating.AgriBusinessRating.DTO.AgriBusinessRatingResponse;
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
public class AgriBusinessRatingService {
    private final AgriBusinessRatingRepository agriBusinessRatingRepository;
    private final AgriBusinessRepository agriBusinessRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> createAgriBusinessRating(AgriBusinessRatingRequest agriBusinessRatingRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            System.out.println(agriBusinessRatingRequest.getStars());

            if (agriBusinessRatingRequest.getStars() < 1 || agriBusinessRatingRequest.getStars() > 5) {
                return new ApiResponse<>("Rating value must be between 1 and 5.", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<AgriBusiness> agriBusinessOptional = agriBusinessRepository.findByDeletedFlagAndId(Constants.NO,
                    agriBusinessRatingRequest.getAgriBusinessId());
            AgriBusiness agriBusiness = agriBusinessOptional.orElse(null);
            if (agriBusiness == null) {
                return new ApiResponse<>("AgriBusiness not found", null, HttpStatus.NOT_FOUND.value());
            }

            AgriBusinessRating agriBusinessRating = new AgriBusinessRating();
            agriBusinessRating.setUser(currentUser);
            agriBusinessRating.setAgriBusiness(agriBusiness);
            agriBusinessRating.setStars(agriBusinessRatingRequest.getStars());
            agriBusinessRating.setComment(agriBusinessRatingRequest.getComment());

            // Save the rating to the database
            agriBusinessRatingRepository.save(agriBusinessRating);

            AgriBusinessRatingResponse agriBusinessRatingResponse = modelMapper.map(agriBusinessRating, AgriBusinessRatingResponse.class);

            return new ApiResponse<AgriBusinessRatingResponse>("AgriBusiness rating submitted successfully.",
                    agriBusinessRatingResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {

            // Handling exceptions
            log.error("An error occurred while adding AgriBusiness rating.", e);

            return new ApiResponse<>("An error occurred while adding AgriBusiness rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> getAllAgriBusinessRating() {
        try {
            List<AgriBusinessRating> agriBusinessRatingList = agriBusinessRatingRepository.findByDeletedFlag(Constants.NO);

            List<AgriBusinessRatingResponse> agriBusinessRatingResponses = agriBusinessRatingList
                    .stream()
                    .map(agriBusinessRating -> modelMapper.map(agriBusinessRating, AgriBusinessRatingResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("AgriBusiness ratings fetched successfully", agriBusinessRatingResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching AgriBusiness ratings.", e);

            return new ApiResponse<>("An error occurred while fetching AgriBusiness ratings.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAgriBusinessRatingsByUserId(Long userId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                List<AgriBusinessRating> agriBusinessRatingsByUser = agriBusinessRatingRepository.findByUser(user);

                List<AgriBusinessRatingResponse> agriBusinessRatingResponseList = agriBusinessRatingsByUser.stream()
                        .map(agriBusinessRating -> modelMapper.map(agriBusinessRating, AgriBusinessRatingResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("AgriBusiness ratings fetched successfully.", agriBusinessRatingResponseList,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching AgriBusiness ratings.", e);
            return new ApiResponse<>("An error occurred while fetching AgriBusiness ratings.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAgriBusinessRatingByAgriBusinessId(Long id) {
        try {
            Optional<AgriBusinessRating> agriBusinessRatingOptional = agriBusinessRatingRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (agriBusinessRatingOptional.isPresent()) {
                AgriBusinessRating agriBusinessRating = agriBusinessRatingOptional.get();

                AgriBusinessRatingResponse agriBusinessRatingResponse = modelMapper.map(agriBusinessRating, AgriBusinessRatingResponse.class);

                return new ApiResponse<>("AgriBusiness rating fetched successfully.", agriBusinessRatingResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("AgriBusiness rating not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching AgriBusiness rating.", e);

            return new ApiResponse<>("An error occurred while fetching AgriBusiness rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> deleteAgriBusinessRating(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<AgriBusinessRating> agriBusinessRatingOptional = agriBusinessRatingRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (agriBusinessRatingOptional.isPresent()) {

                AgriBusinessRating agriBusinessRating = agriBusinessRatingOptional.get();
                agriBusinessRating.setDeletedFlag(Constants.YES);
                agriBusinessRating.setDeletedAt(LocalDateTime.now());
                agriBusinessRating.setAddedBy(currentUser);

                agriBusinessRatingRepository.save(agriBusinessRating);

                return new ApiResponse<>("AgriBusiness rating was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("AgriBusiness rating not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting AgriBusiness rating.", e);

            return new ApiResponse<>("An error occurred while deleting AgriBusiness rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
