package com.EquiFarm.EquiFarm.user;

import com.EquiFarm.EquiFarm.Farmer.DTO.UserResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final ModelMapper modelMapper;
    public ApiResponse<?> fetchAllActors(){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("User unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            List<User> userList = repository.findByDeletedFlag(Constants.NO);
            List<UserResponse> userResponses = userList
                    .stream()
                    .map(user -> modelMapper
                            .map(user, UserResponse.class))
                    .toList();
            return new ApiResponse<>("Users fetched successfully", userResponses, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error: ", e);
            return new ApiResponse<>("An Error occured", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> fetchActorById(Long userId){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("User unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<User> userOptional = repository.findByDeletedFlagAndId(Constants.NO, userId);
            if (userOptional.isEmpty()){
                return new ApiResponse<>("User not found", null, HttpStatus.NOT_FOUND.value());
            }
            User user = userOptional.get();
            UserResponse userResponse = modelMapper.map(user, UserResponse.class);
            return new ApiResponse<>("Users fetched successfully", userResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error: ", e);
            return new ApiResponse<>("An Error occured", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
