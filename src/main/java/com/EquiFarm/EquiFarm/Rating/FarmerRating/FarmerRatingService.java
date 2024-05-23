package com.EquiFarm.EquiFarm.Rating.FarmerRating;

import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.EquiFarm.EquiFarm.Rating.FarmerRating.DTO.FarmerRatingRequest;
import com.EquiFarm.EquiFarm.Rating.FarmerRating.DTO.FarmerRatingResponse;
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
public class FarmerRatingService {
    private final FarmerRatingRepository farmerRatingRepository;
    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> createFarmerRating(FarmerRatingRequest farmerRatingRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            System.out.println(farmerRatingRequest.getStars());

            if (farmerRatingRequest.getStars() < 1 || farmerRatingRequest.getStars() > 5) {
                return new ApiResponse<>("Rating value must be between 1 and 5.", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndId(Constants.NO, farmerRatingRequest.getFarmerId());
            Farmer farmer = farmerOptional.orElse(null);
            if (farmer == null) {
                return new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value());
            }

            FarmerRating farmerRating = new FarmerRating();
            farmerRating.setUser(currentUser);
            farmerRating.setFarmer(farmer);
            farmerRating.setStars(farmerRatingRequest.getStars());
            farmerRating.setComment(farmerRatingRequest.getComment());

            // Save the rating to the database
            farmerRatingRepository.save(farmerRating);

            FarmerRatingResponse farmerRatingResponse = modelMapper.map(farmerRating, FarmerRatingResponse.class);

            return new ApiResponse<FarmerRatingResponse>("Farmer rating submitted successfully.",
                    farmerRatingResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {

            // Handling exceptions
            log.error("An error occurred while adding farmer rating.", e);

            return new ApiResponse<>("An error occurred while adding farmer rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    @Transactional
    public ApiResponse<?> getAverageFarmerRatingByFarmerId(Long farmerId) {
        try {
            List<FarmerRating> farmerRatingList = farmerRatingRepository.findByFarmerIdAndDeletedFlag(farmerId,Constants.NO);

            // Calculate the average rating
            double averageRating = calculateAverageRating(farmerRatingList);

            return new ApiResponse<>("Average farmer rating fetched successfully", averageRating, HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching farmer ratings for farmer Id: " + farmerId, e);

            return new ApiResponse<>("An error occurred while fetching farmer ratings.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private double calculateAverageRating(List<FarmerRating> farmerRatingList) {
        if (farmerRatingList.isEmpty()) {
            return 0.0; // Return 0 if there are no ratings.
        }

        // Calculate the average rating using Java Streams.
        double average = farmerRatingList.stream()
                .mapToDouble(FarmerRating::getStars)
                .average()
                .orElse(0.0); // Return 0 if there's no average (empty list).

        return average;
    }


    public ApiResponse<?> getAllFarmerRating() {
        try {
            List<FarmerRating> farmerRatingList = farmerRatingRepository.findByDeletedFlag(Constants.NO);

            List<FarmerRatingResponse> farmerRatingResponses = farmerRatingList
                    .stream()

                    .map(farmerRating -> modelMapper.map(farmerRating, FarmerRatingResponse.class))
                    .collect(Collectors.toList());
            return new ApiResponse<>("farmer rating fetched successfully", farmerRatingResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching Driver Rating.", e);

            return new ApiResponse<>("An error occurred while fetching farmer rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }


    }

    @Transactional
    public ApiResponse<?> getFarmerRatingByFarmerId(Long id) {
        try {
            Optional<FarmerRating> farmerRatingOptional = farmerRatingRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (farmerRatingOptional.isPresent()) {
                FarmerRating farmerRating = farmerRatingOptional.get();

                FarmerRatingResponse farmerRatingResponse = modelMapper.map(farmerRating, FarmerRatingResponse.class);

                return new ApiResponse<>("Farmer rating fetched successfully.", farmerRatingResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Farmer rating not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching farmer rating.", e);

            return new ApiResponse<>("An error occurred while fetching farmer rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getFarmerRatingsByUserId(Long userId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                List<FarmerRating> farmerRatingsByUser = farmerRatingRepository.findByUser(user);

                List<FarmerRatingResponse> farmerRatingResponseList = farmerRatingsByUser.stream()
                        .map(farmerRating -> modelMapper.map(farmerRating, FarmerRatingResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("Farmer ratings fetched successfully.", farmerRatingResponseList,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching farmer ratings.", e);
            return new ApiResponse<>("An error occurred while fetching farmer ratings.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> deleteFarmerRating(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied.User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<FarmerRating> farmerRatingOptional = farmerRatingRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (farmerRatingOptional.isPresent()) {

                FarmerRating farmerRating = farmerRatingOptional.get();
                farmerRating.setDeletedFlag(Constants.YES);
                farmerRating.setDeletedAt(LocalDateTime.now());
                farmerRating.setAddedBy(currentUser);

                farmerRatingRepository.save(farmerRating);

                return new ApiResponse<>("farmer rating was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("farmer rating not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting farmer rating.", e);

            return new ApiResponse<>("An error occurred while deleting farmer rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


}
