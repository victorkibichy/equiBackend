package com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProdDisplayImage;

import com.EquiFarm.EquiFarm.AgriBusiness.AgriBusinessProducts.AgriBusinessProductRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImagesRepository;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProductsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class AgriBusinessDisplayImageService {
    private final AgriBusinessDisplayImageRepository agriBusinessDisplayImageRepository;
    private final AgriBusinessProductRepository agriBusinessProductRepository;



}
