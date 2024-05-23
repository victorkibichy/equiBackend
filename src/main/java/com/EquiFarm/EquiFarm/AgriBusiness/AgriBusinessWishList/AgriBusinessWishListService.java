package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProduct;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductRepository;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList.DTO.AgriBusinessWishListRequest;
import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessWishList.DTO.AgriBusinessWishListResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgriBusinessWishListService {
    private final AgriBusinessWishListRepository agriBusinessWishListRepository;
    private final AgriBusinessProductRepository agriBusinessProductRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> getAllAgriBusinessWishLists() {
        try {

            List<AgriBusinessWishList> agriBusinessWishLists = agriBusinessWishListRepository.findByDeletedFlag(Constants.NO);

            List<AgriBusinessWishListResponse> agriBusinessWishListResponseList = agriBusinessWishLists.stream()
                    .map(agriBusinessWishLists1 -> modelMapper.map(agriBusinessWishLists1, AgriBusinessWishListResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("AgriBusiness Wish list fetched successfully.", agriBusinessWishListResponseList, HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching AgriBusiness WishLists.", e);

            return new ApiResponse<>("An error occurred while fetching AgriBusiness WishLists.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getUserAgriBusinessWishList() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusinessWishList> agriBusinessWishListOptional = agriBusinessWishListRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (agriBusinessWishListOptional .isPresent()) {
                AgriBusinessWishList agriBusinessWishList = agriBusinessWishListOptional.get();

                AgriBusinessWishListResponse agriBusinessWishListResponse = modelMapper.map(agriBusinessWishList, AgriBusinessWishListResponse.class);

                return new ApiResponse<>("User Agribusiness WishList fetched successfully.", agriBusinessWishListResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Agribusines WishList not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching user AgriBusiness WishList.", e);
            return new ApiResponse<>("An error occurred while fetching user Agribusiness WishList.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> addProductToAgriBusinessWishList(AgriBusinessWishListRequest agriBusinessWishListRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusinessWishList> agriBusinessWishListOptional = agriBusinessWishListRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            System.out.println("Found wish list++" + agriBusinessWishListRequest.getAgriBusinessProductsId());

            if (agriBusinessWishListOptional.isPresent()) {
                AgriBusinessWishList agriBusinessWishList = agriBusinessWishListOptional.get();

                if (agriBusinessWishListRequest.getAgriBusinessProductsId() != null && !agriBusinessWishListRequest.getAgriBusinessProductsId().isEmpty()) {

                    List<AgriBusinessProduct> agriBusinessProducts = agriBusinessProductRepository
                            .findAllById(agriBusinessWishListRequest.getAgriBusinessProductsId());

                    List<AgriBusinessProduct> existingAgriBusinessProducts = agriBusinessWishList.getAgriBusinessProducts();

                    // Combine the existing agribusiness products with the existing
                    List<AgriBusinessProduct> allAgriBusinessProducts = Stream.concat(
                            agriBusinessProducts.stream(),
                            existingAgriBusinessProducts.stream()).distinct().collect(Collectors.toList());

                    // Remove deleted agriBusiness products
                    List<AgriBusinessProduct> addAgriBusinessProducts = allAgriBusinessProducts.stream()
                            .filter(prod -> prod.getDeletedFlag() == Constants.NO)
                            .collect(Collectors.toList());

                    agriBusinessWishList.setAgriBusinessProducts(addAgriBusinessProducts);

                }

                AgriBusinessWishList updatedAgriBusinessWishList = agriBusinessWishListRepository.save(agriBusinessWishList);

                AgriBusinessWishListResponse agriBusinessWishListResponse = modelMapper.map(updatedAgriBusinessWishList, AgriBusinessWishListResponse.class);

                return new ApiResponse<>("product was successfully added to the agribusiness wishlist.",
                        agriBusinessWishListResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Agribusiness wishList not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while adding product to agribusiness wishLists.", e);

            return new ApiResponse<>("An error occurred while adding product to agribusiness wishLists.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @Transactional
    public ApiResponse<?> removeProductFromAgriBusinessWishList(AgriBusinessWishListRequest agriBusinessWishListRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusinessWishList> agriBusinessWishListOptional = agriBusinessWishListRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (agriBusinessWishListOptional.isPresent()) {
                AgriBusinessWishList agriBusinessWishList = agriBusinessWishListOptional.get();

                if (agriBusinessWishListRequest.getAgriBusinessProductsId() != null && !agriBusinessWishListRequest.getAgriBusinessProductsId().isEmpty()) {
                    List<AgriBusinessProduct> agriBusinessProducts = agriBusinessProductRepository
                            .findAllById(agriBusinessWishListRequest.getAgriBusinessProductsId());

                    if (!agriBusinessProducts.isEmpty()) {
                        // Remove deleted agribusiness products
                        List<AgriBusinessProduct> agriBusinessProductsToRemove = agriBusinessProducts.stream()
                                .filter(prod -> prod.getDeletedFlag() == Constants.NO)
                                .collect(Collectors.toList());

                        agriBusinessWishList.getAgriBusinessProducts().removeAll(agriBusinessProductsToRemove);
                    }
                }

                AgriBusinessWishList updatedAgriBusinessWishList = agriBusinessWishListRepository.save(agriBusinessWishList);

                AgriBusinessWishListResponse agriBusinessWishListResponse = modelMapper.map(updatedAgriBusinessWishList, AgriBusinessWishListResponse.class);

                return new ApiResponse<>("Product was successfully removed.", agriBusinessWishListResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Agribusiness wish list not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while removing product from agribusiness wish list.", e);
            return new ApiResponse<>("An error occurred while removing product from agribusiness wish list.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    @Transactional
    public ApiResponse<?> agriBusinessWishListDelete (Long id){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<AgriBusinessWishList> agriBusinessWishListOptional = agriBusinessWishListRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (agriBusinessWishListOptional.isPresent()) {
                AgriBusinessWishList agriBusinessWishList = agriBusinessWishListOptional.get();

                agriBusinessWishList.setDeletedAt(LocalDateTime.now());
                agriBusinessWishList.setDeletedBy(currentUser);
                agriBusinessWishList.setDeletedFlag(Constants.YES);

                agriBusinessWishListRepository.save(agriBusinessWishList);

                return new ApiResponse<>("Agribusiness wishlist was successfully deleted.", null, HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Agribusiness wishlist not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while deleting agribusiness wishlist.", e);
            return new ApiResponse<>("An error occurred while deleting agribusiness wishlist.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}




