package com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices;

import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.DTO.ProvidedServicesRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.DTO.ProvidedServicesResponse;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProviderRepository;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProviderService;
import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.TypeOfServices;
import com.EquiFarm.EquiFarm.ServiceProvider.TypeOfServices.TypeOfServicesRepository;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProvidedServicesService {
    private final ProvidedServicesRepo providedServicesRepo;
    private final ServiceProviderRepository serviceProviderRepository;
    private final TypeOfServicesRepository typeOfServicesRepository;
    private final ModelMapper modelMapper;

    public ApiResponse<?> createProvidedServices(Long typeOfServiceId,
                                                 ProvidedServicesRequest providedServicesRequest){
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null){
                return new ApiResponse<>("Access Denied! User is unauthenticated", null, HttpStatus.UNAUTHORIZED.value());
            }
            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
            if (serviceProviderOptional.isEmpty()){
                System.out.println(currentUser.getId());
                return new ApiResponse<>("Service Provider not found", null, HttpStatus.NOT_FOUND.value());
            }
            try {
                ServiceProvider serviceProvider = serviceProviderOptional.get();

                Optional<TypeOfServices> typeOfServicesOptional = typeOfServicesRepository.findByDeletedFlagAndId(Constants.NO, typeOfServiceId);
                if (typeOfServicesOptional.isEmpty()){
                    return new ApiResponse<>("Type of Service not found", null, HttpStatus.NOT_FOUND.value());
                }
                TypeOfServices typeOfServices = typeOfServicesOptional.get();

                ProvidedServices providedServices = new ProvidedServices();
                providedServices.setServiceProvider(serviceProvider);
                providedServices.setTypeOfServices(typeOfServices);
                providedServices.setDescription(providedServicesRequest.getDescription());
                providedServices.setPrice(providedServicesRequest.getPrice());
                ProvidedServices savedServices = providedServicesRepo.save(providedServices);
                ProvidedServicesResponse providedServicesResponse = modelMapper.map(savedServices, ProvidedServicesResponse.class);
                return new ApiResponse<>("Success adding provided services details", providedServicesResponse, HttpStatus.OK.value());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e){
            log.info("Error Creating Provided Services: ", e);
            return new ApiResponse<>("Error Creating Provided Services", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
//    public ApiResponse<?> fetchProviderServices(Long serviceProviderId){
//        try {
//            Optional<ServiceProvider> serviceProviderOptional = serviceProviderRepository.findByDeletedFlagAndId(Constants.NO, serviceProviderId);
//            if (serviceProviderOptional.isEmpty()){
//                return new ApiResponse<>("Service Provider not found", null, HttpStatus.NOT_FOUND.value());
//            }
//            ServiceProvider serviceProvider = serviceProviderOptional.get();
//            List<ProvidedServices> providedServicesList = providedServicesRepo.findByServiceProvider(serviceProvider);
//            List<ProvidedServicesResponse> providedServicesResponseList = providedServicesList
//                    .stream()
//                    .map(service-> modelMapper
//                            .map(service, ProvidedServicesResponse.class))
//                    .toList();
//            return new ApiResponse<>("Success retrieving the services", providedServicesResponseList, HttpStatus.OK.value());
//
//
//        } catch (Exception e){
//            log.info("error retrieving services: ", e);
//            return new ApiResponse<>("error retrieving services", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//
//    }

    public ApiResponse<?> fetchAll(Long serviceProviderId){
        try {
            List<ProvidedServices> providedServicesList = providedServicesRepo.findByDeletedFlag(Constants.NO);
            List<ProvidedServicesResponse> providedServicesResponseList = providedServicesList
                    .stream()
                    .filter(service-> (serviceProviderId == null || service.getServiceProvider().getId().equals(serviceProviderId)))
                    .map(service -> modelMapper
                            .map(service, ProvidedServicesResponse.class))
                    .toList();
            return new ApiResponse<>("Success retrieving services", providedServicesResponseList, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error fetching all services: ", e);
            return new ApiResponse<>("Error fetching all services", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> fetchById(Long serviceId){
        try {
            Optional<ProvidedServices> providedServicesOptional = providedServicesRepo.findByIdAndDeletedFlag(serviceId, Constants.NO);
            if(providedServicesOptional.isEmpty()){
                return new ApiResponse<>("Provided Service not found", null,HttpStatus.NOT_FOUND.value());
            }
            ProvidedServices providedServices = providedServicesOptional.get();
            ProvidedServicesResponse providedServicesResponse = modelMapper.map(providedServices, ProvidedServicesResponse.class);
            return new ApiResponse<>("Success retrieving the service", providedServicesResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error fetching by Id: ", e);
            return new ApiResponse<>("Error fetching by Id", null,HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateProvidedService(Long serviceId, ProvidedServicesRequest providedServicesRequest){
        try {
            Optional<ProvidedServices> providedServicesOptional = providedServicesRepo.findByIdAndDeletedFlag(serviceId, Constants.NO);
            if (providedServicesOptional.isEmpty()){
                return new ApiResponse<>("Provided Service Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            ProvidedServices providedServices = providedServicesOptional.get();
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(providedServicesRequest.getPrice(),
                    providedServices::setPrice, providedServices::getPrice);
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(providedServicesRequest.getDescription(),
                    providedServices::setDescription, providedServices::getDescription);
            ProvidedServices savedService = providedServicesRepo.save(providedServices);
            ProvidedServicesResponse providedServicesResponse = modelMapper.map(savedService, ProvidedServicesResponse.class);
            return new ApiResponse<>("Success updating the provided service", providedServicesResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error updating provided Service: ", e);
            return new ApiResponse<>("Error updating provided Service", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteService(Long serviceId){
        try {
            Optional<ProvidedServices> providedServicesOptional = providedServicesRepo.findByIdAndDeletedFlag(serviceId, Constants.NO);
            if (providedServicesOptional.isEmpty()){
                return new ApiResponse<>("Service not found", null, HttpStatus.NOT_FOUND.value());
            }
            ProvidedServices providedServices = providedServicesOptional.get();
            providedServices.setDeletedAt(LocalDateTime.now());
            providedServices.setDeletedBy(SecurityUtils.getCurrentUser());
            providedServices.setDeletedFlag(Constants.YES);
            providedServicesRepo.save(providedServices);
            return new ApiResponse<>("Success deleting a provided service", null, HttpStatus.NO_CONTENT.value());

        } catch (Exception e){
            log.info("Error deleting a provided service: ", e);
            return new ApiResponse<>("Error deleting a provided service", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
