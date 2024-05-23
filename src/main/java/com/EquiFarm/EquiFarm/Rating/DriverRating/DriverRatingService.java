package com.EquiFarm.EquiFarm.Rating.DriverRating;

import com.EquiFarm.EquiFarm.Driver.Driver;
import com.EquiFarm.EquiFarm.Driver.DriverRepository;
import com.EquiFarm.EquiFarm.Rating.DriverRating.DTO.DriverRatingRequest;
import com.EquiFarm.EquiFarm.Rating.DriverRating.DTO.DriverRatingResponse;
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
public class DriverRatingService {
    private final DriverRatingRepository driverRatingRepository;
    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse <?> createDriverRating(DriverRatingRequest driverRatingRequest){
        try{
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null){
                return new ApiResponse <>("Access denied.User is not authenticated.",null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            System.out.println(driverRatingRequest.getStars());

            if (driverRatingRequest.getStars() < 1 || driverRatingRequest.getStars() > 5) {
                return new ApiResponse<>("Rating value must be between 1 and 5.", null, HttpStatus.BAD_REQUEST.value());
            }

            Optional<Driver> driverOptional = driverRepository.findByDeletedFlagAndId(Constants.NO, driverRatingRequest.getDriverId());
            Driver driver = driverOptional.orElse(null);
            if (driver == null){
                return new ApiResponse<>("driver not found",null,HttpStatus.NOT_FOUND.value());
            }

            DriverRating driverRating = new DriverRating();
            driverRating.setUser(currentUser);
            driverRating.setDriver(driver);
            driverRating.setStars(driverRatingRequest.getStars());
            driverRating.setComment(driverRatingRequest.getComment());



            // Save the rating to the database
            driverRatingRepository.save(driverRating);

            DriverRatingResponse driverRatingResponse = modelMapper.map(driverRating, DriverRatingResponse.class);

            return new ApiResponse<DriverRatingResponse>("Driver rating submitted successfully.",
                    driverRatingResponse,
                    HttpStatus.CREATED.value());

        } catch (Exception e) {

            // Handling exceptions
            log.error("An error occurred while adding rating.", e);

            return new ApiResponse<>("An error occurred while adding rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getAllDriverRating() {
        try {
            List<DriverRating> driverRatingList = driverRatingRepository.findByDeletedFlag(Constants.NO);

            List<DriverRatingResponse> driverRatingResponses = driverRatingList
                    .stream()

                    .map(driverRating -> modelMapper.map(driverRating, DriverRatingResponse.class))
                    .collect(Collectors.toList());
            return new ApiResponse<>("driver rating fetched successfully", driverRatingResponses,
                    HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching Driver Rating.", e);

            return new ApiResponse<>("An error occurred while fetching driver rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @Transactional
    public ApiResponse<?> getDriverRatingByDriverId(Long id) {
        try {
            Optional<DriverRating> driverRatingOptional = driverRatingRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (driverRatingOptional.isPresent()) {
                DriverRating driverRating = driverRatingOptional.get();

                DriverRatingResponse driverRatingResponse = modelMapper.map(driverRating, DriverRatingResponse.class);

                return new ApiResponse<>("Driver rating fetched successfully.", driverRatingResponse,
                        HttpStatus.OK.value());
            } else {
                return new ApiResponse<>("Driver rating not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching driver rating.", e);

            return new ApiResponse<>("An error occurred while fetching driver rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> getDriverRatingsByUserId(Long userId) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();

            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                List<DriverRating> driverRatingsByUser = driverRatingRepository.findByUser(user);

                List<DriverRatingResponse> driverRatingResponseList = driverRatingsByUser.stream()
                        .map(driverRating -> modelMapper.map(driverRating, DriverRatingResponse.class))
                        .collect(Collectors.toList());

                return new ApiResponse<>("Driver ratings fetched successfully.", driverRatingResponseList,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("User not found.", null, HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching driver ratings.", e);
            return new ApiResponse<>("An error occurred while fetching driver ratings.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    public ApiResponse<?> deleteDriverRating(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied.User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }
            Optional<DriverRating> driverRatingOptional = driverRatingRepository
                    .findByDeletedFlagAndId(Constants.NO, id);
            if (driverRatingOptional.isPresent()) {

                DriverRating driverRating = driverRatingOptional.get();
                driverRating.setDeletedFlag(Constants.YES);
                driverRating.setDeletedAt(LocalDateTime.now());
                driverRating.setAddedBy(currentUser);

                driverRatingRepository.save(driverRating);

                return new ApiResponse<>("driver rating was successfully deleted.", null,
                        HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("driver rating not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while deleting driver rating.", e);

            return new ApiResponse<>("An error occurred while deleting driver rating.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}