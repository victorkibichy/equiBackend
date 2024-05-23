package com.EquiFarm.EquiFarm.WishList;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProducts;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;
import com.EquiFarm.EquiFarm.WishList.DTO.WishListRequest;
import com.EquiFarm.EquiFarm.WishList.DTO.WishListResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishListService {
    private final WishListRepository wishListRepository;
    private final FarmProductsRepository farmProductsRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ApiResponse<?> getUserWishList() {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<WishList> wishListOptional = wishListRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (wishListOptional.isPresent()) {
                WishList wishList = wishListOptional.get();

                WishListResponse wishListResponse = modelMapper.map(wishList, WishListResponse.class);

                return new ApiResponse<>("User WishList fetched successfully.", wishListResponse,
                        HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("WishList not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while fetching user WishList.", e);
            return new ApiResponse<>("An error occurred while fetching user WishList.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> getAllWishLists() {
        try {

            List<WishList> wishList = wishListRepository.findByDeletedFlag(Constants.NO);

            List<WishListResponse> wishListResponseList = wishList.stream()
                    .map(wishLst -> modelMapper.map(wishLst, WishListResponse.class))
                    .collect(Collectors.toList());

            return new ApiResponse<>("Wish list fetched successfully.", wishListResponseList, HttpStatus.OK.value());

        } catch (Exception e) {
            log.error("An error occurred while fetching WishLists.", e);

            return new ApiResponse<>("An error occurred while fetching WishLists.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> addProductToWishList(WishListRequest wishListRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<WishList> wishListOptional = wishListRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            System.out.println("Found wish list++" + wishListRequest.getFarmProductsId());

            if (wishListOptional.isPresent()) {
                WishList wishList = wishListOptional.get();

                if (wishListRequest.getFarmProductsId() != null && !wishListRequest.getFarmProductsId().isEmpty()) {

                    List<FarmProducts> farmProducts = farmProductsRepository
                            .findAllById(wishListRequest.getFarmProductsId());

                    List<FarmProducts> existingFarmProducts = wishList.getFarmproducts();

                    // Combine the existing farm products with the existing
                    List<FarmProducts> allFarmProducts = Stream.concat(
                            farmProducts.stream(),
                            existingFarmProducts.stream()).distinct().collect(Collectors.toList());

                    // Remove deleted farm products
                    List<FarmProducts> addFarmProducts = allFarmProducts.stream()
                            .filter(prod -> prod.getDeletedFlag() == Constants.NO)
                            .collect(Collectors.toList());

                    wishList.setFarmproducts(addFarmProducts);

                }

                WishList updatedWishList = wishListRepository.save(wishList);

                WishListResponse wishListResponse = modelMapper.map(updatedWishList, WishListResponse.class);

                return new ApiResponse<>("product was successfully added to the wishlist.",
                        wishListResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("WishList not found.", null,
                        HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while adding product to WishLists.", e);

            return new ApiResponse<>("An error occurred while adding product to WishLists.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @Transactional
    public ApiResponse<?> removeProductFromWishList(WishListRequest wishListRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<WishList> wishListOptional = wishListRepository.findByDeletedFlagAndUserId(Constants.NO,
                    currentUser.getId());

            if (wishListOptional.isPresent()) {
                WishList wishList = wishListOptional.get();

                if (wishListRequest.getFarmProductsId() != null && !wishListRequest.getFarmProductsId().isEmpty()) {
                    List<FarmProducts> farmProducts = farmProductsRepository
                            .findAllById(wishListRequest.getFarmProductsId());

                    if (!farmProducts.isEmpty()) {
                        // Remove deleted farm products
                        List<FarmProducts> farmProductsToRemove = farmProducts.stream()
                                .filter(prod -> prod.getDeletedFlag() == Constants.NO)
                                .collect(Collectors.toList());

                        wishList.getFarmproducts().removeAll(farmProductsToRemove);
                    }
                }

                WishList updatedWishList = wishListRepository.save(wishList);

                WishListResponse wishListResponse = modelMapper.map(updatedWishList, WishListResponse.class);

                return new ApiResponse<>("Product was successfully removed.", wishListResponse, HttpStatus.OK.value());

            } else {
                return new ApiResponse<>("Wish list not found.", null, HttpStatus.NOT_FOUND.value());
            }

        } catch (Exception e) {
            log.error("An error occurred while removing product from wish list.", e);
            return new ApiResponse<>("An error occurred while removing product from wish list.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Transactional
    public ApiResponse<?> wishListDelete(Long id) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Access denied. User is not authenticated.", null,
                        HttpStatus.UNAUTHORIZED.value());
            }

            Optional<WishList> wishListOptional = wishListRepository.findByDeletedFlagAndId(Constants.NO, id);

            if (wishListOptional.isPresent()) {
                WishList wishList = wishListOptional.get();

                wishList.setDeletedAt(LocalDateTime.now());
                wishList.setDeletedBy(currentUser);
                wishList.setDeletedFlag(Constants.YES);

                wishListRepository.save(wishList);

                return new ApiResponse<>("Wishlist was successfully deleted.", null, HttpStatus.NO_CONTENT.value());

            } else {
                return new ApiResponse<>("Wishlist not found.", null, HttpStatus.NOT_FOUND.value());

            }
        } catch (Exception e) {
            log.error("An error occurred while deleting wishlist.", e);
            return new ApiResponse<>("An error occurred while deleting wishlist.", null,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
