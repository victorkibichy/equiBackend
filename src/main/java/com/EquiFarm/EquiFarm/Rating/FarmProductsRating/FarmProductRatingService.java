package com.EquiFarm.EquiFarm.Rating.FarmProductsRating;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;

import com.EquiFarm.EquiFarm.Rating.FarmProductsRating.DTO.FarmProductRatingRequest;
import com.EquiFarm.EquiFarm.Rating.FarmProductsRating.DTO.FarmProductRatingResponse;

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
public class FarmProductRatingService {
    private final FarmProductRatingRepository farmProductRatingRepository;
    private final FarmProductsRepository farmProductsRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> createFarmProductRating(FarmProductRatingRequest farmProductRatingRequest){
        try{
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null){
                return new ApiResponse <>("Access denied.User is not authenticated.",null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            if (farmProductRatingRequest.getStars() < 1 || farmProductRatingRequest.getStars() > 5) {
                return new ApiResponse<>("Rating value must be between 1 and 5.",null,
                        HttpStatus.BAD_REQUEST.value());
            }

            Optional<FarmProducts> farmProductsOptional = farmProductsRepository.findByDeletedFlagAndId(
                    Constants.NO, farmProductRatingRequest.getFarmProductId());
            FarmProducts farmProduct = farmProductsOptional.orElse(null);
            if (farmProduct == null){
                return new ApiResponse<>("Farm product not found",null,HttpStatus.NOT_FOUND.value());
            }

            FarmProductRating farmProductRating = new FarmProductRating();
            farmProductRating.setUser(currentUser);
            farmProductRating.setFarmProduct(farmProduct);
            farmProductRating.setStars(farmProductRatingRequest.getStars());
            farmProductRating.setComment(farmProductRatingRequest.getComment());

            farmProductRatingRepository.save(farmProductRating);

            FarmProductRatingResponse farmProductRatingResponse = modelMapper.map(farmProductRating,
                    FarmProductRatingResponse.class);

            return new ApiResponse<FarmProductRatingResponse>("Farm product rating submitted successfully.",
                    farmProductRatingResponse, HttpStatus.CREATED.value());
        } catch (Exception e) {
            log.error("An error occurred while adding rating.", e);

            return new ApiResponse<>("An error occurred while adding rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAverageFarmProductRatingByFarmProductId(Long farmProductId) {
        try {
            List<FarmProductRating> farmProductRatingList = farmProductRatingRepository
                    .findByFarmProductIdAndDeletedFlag(farmProductId,Constants.NO);

            // Calculate the average rating
            double averageRating = calculateAverageRating(farmProductRatingList);

            return new ApiResponse<>("Average farm product rating fetched successfully", averageRating,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching farm product  ratings for farmer Id: " + farmProductId, e);

            return new ApiResponse<>("An error occurred while fetching farm product  ratings.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private double calculateAverageRating(List<FarmProductRating> farmProductRatingList) {
        if (farmProductRatingList.isEmpty()) {
            return 0.0; // Return 0 if there are no ratings.
        }

        // Calculate the average rating using Java Streams.
        double average = farmProductRatingList.stream()
                .mapToDouble(FarmProductRating::getStars)
                .average()
                .orElse(0.0); // Return 0 if there's no average (empty list).

        return average;
    }

    @Transactional
    public ApiResponse<?> getAllFarmProductsRating() {
        try {
            List<FarmProductRating> farmProductRatingList = farmProductRatingRepository
                    .findByDeletedFlag(Constants.NO);

            List<FarmProductRatingResponse> farmProductRatingResponses = farmProductRatingList
                    .stream().map(farmProductRating -> modelMapper.map(farmProductRating,
                            FarmProductRatingResponse.class)).collect(Collectors.toList());

            return new ApiResponse<>("Farm products rating fetched successfully",
                    farmProductRatingResponses, HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching farm products rating.", e);

            return new ApiResponse<>("An error occurred while fetching farm products rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @Transactional
    public ApiResponse<?> getFarmProductRatingByFarmProductId(Long farmProductId) {
        try {
            Optional<FarmProducts> farmProductOptional = farmProductsRepository.findByDeletedFlagAndId(
                    Constants.NO, farmProductId);

            if (farmProductOptional.isPresent()) {
                FarmProducts farmProduct = farmProductOptional.get();

                List<FarmProductRating> farmProductRatingList = farmProductRatingRepository
                        .findByDeletedFlagAndFarmProductId(Constants.NO, farmProduct.getId());

                List<FarmProductRatingResponse> farmProductRatingResponses = farmProductRatingList
                        .stream().map(farmProductRating -> modelMapper.map(farmProductRating,
                                FarmProductRatingResponse.class)).collect(Collectors.toList());

                return new ApiResponse<>("Farm products rating fetched successfully",
                        farmProductRatingResponses, HttpStatus.OK.value());
            }
            else {
                return new ApiResponse<>("Farm product not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching farm product rating.", e);

            return new ApiResponse<>("An error occurred while fetching farm product rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getFarmProductRatingByUserId(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findByDeletedFlagAndId(
                    Constants.NO, userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                List<FarmProductRating> farmProductRatingList = farmProductRatingRepository
                        .findByDeletedFlagAndUserId(Constants.NO, user.getId());

                List<FarmProductRatingResponse> farmProductRatingResponses = farmProductRatingList
                        .stream().map(farmProductRating -> modelMapper.map(farmProductRating,
                                FarmProductRatingResponse.class)).collect(Collectors.toList());

                return new ApiResponse<>("Farm products rating fetched successfully",
                        farmProductRatingResponses, HttpStatus.OK.value());
            }
            else {
                return new ApiResponse<>("Farm product not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching farm product rating.", e);

            return new ApiResponse<>("An error occurred while fetching farm product rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> deleteFarmProductRating(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied.User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<FarmProductRating> farmProductRatingOptional = farmProductRatingRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (farmProductRatingOptional.isPresent()) {

                FarmProductRating farmProductRating = farmProductRatingOptional.get();
                farmProductRating.setDeletedFlag(Constants.YES);
                farmProductRating.setDeletedAt(LocalDateTime.now());
                farmProductRating.setAddedBy(currentUser);

                farmProductRatingRepository.save(farmProductRating);

                return new ApiResponse<>("Farm product rating was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Farm product rating not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting farm product rating.", e);

            return new ApiResponse<>("An error occurred while deleting farm product rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
