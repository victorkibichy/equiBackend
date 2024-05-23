package com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages;

import org.springframework.stereotype.Service;

import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisplayImagesService {
    private final DisplayImagesRepository displayImagesRepository;
    private final FarmProductsRepository farmProductsRepository;

    // @Transactional
    // public ApiResponse<?> addFarmProductDisplayImage(List<DisplayImagesRequest> displayImagesRequest, Long productId) {
    //     try {
    //         Optional<FarmProducts> farmProductOptional = farmProductsRepository.findByDeletedFlagAndId(Constants.NO,
    //                 productId);

    //         if (farmProductOptional.isPresent()) {
    //         FarmProducts farmProduct = farmProductOptional.get(); 
    //         List<String> newDisplayImages = displayImagesRequest.getDisplayImages();
    //         List<DisplayImages> displayImagesList = new ArrayList<>();

    //         for (String displayImage : newDisplayImages) {
    //             DisplayImages newDisplayImage = new DisplayImages();
    //             newDisplayImage.setDisplayImage(displayImage);
    //             displayImagesList.add(newDisplayImage);
    //         }

    //         farmProduct.setDisplayImages(displayImagesList);
    //         farmProductsRepository.save(farmProduct);

    //         } else {
    //             return new ApiResponse<>("Type of Product already Exists.", null,
    //                     HttpStatus.NOT_FOUND.value());
    //         }

    //     } catch (Exception e) {
    //         log.error("An error occurred while fetching farm", e);

    //         return new ApiResponse<>("An error occurred while fetching farm.", null,
    //                 HttpStatus.INTERNAL_SERVER_ERROR.value());
    //     }

    // }

}
