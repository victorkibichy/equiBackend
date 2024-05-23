package com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest;

import com.EquiFarm.EquiFarm.Farmer.DTO.FarmersResponse;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DTO.DisplayImagesRequest;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImages;
import com.EquiFarm.EquiFarm.Farmer.FarmProducts.Product.FarmProdDisplayImages.DisplayImagesRepository;
import com.EquiFarm.EquiFarm.Farmer.Farmer;
import com.EquiFarm.EquiFarm.Farmer.FarmerRepository;
import com.EquiFarm.EquiFarm.ServiceProvider.Cordinates;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServices;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServicesController;
import com.EquiFarm.EquiFarm.ServiceProvider.ProvidedServices.ProvidedServicesRepo;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProvider;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProviderRepository;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest.DTO.ServiceRequestRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceRequest.DTO.ServiceRequestResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.user.User;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Fetch;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServiceRequestService {
    private final ServiceRequestRepo serviceRequestRepo;
    private final FarmerRepository farmerRepository;
    private final ProvidedServicesRepo providedServicesRepo;
    private final DisplayImagesRepository displayImagesRepository;
    private final ModelMapper modelMapper;
    private final ServiceProviderRepository serviceProviderRepository;

    public ApiResponse<?> makeServiceRequest(Long providedServiceId, ServiceRequestRequest serviceRequestRequest) {
        try {
            User currentUser = SecurityUtils.getCurrentUser();
            if (currentUser == null) {
                return new ApiResponse<>("Unauthenticated User", null, HttpStatus.UNAUTHORIZED.value());
            }
//            Optional<Farmer> farmerOptional = farmerRepository.findByDeletedFlagAndUserId(Constants.NO, currentUser.getId());
//            if (farmerOptional.isEmpty()) {
//                return new ApiResponse<>("Farmer not found", null, HttpStatus.NOT_FOUND.value());
//            }
//            Farmer farmer = farmerOptional.get();

            Optional<ProvidedServices> providedServicesOptional = providedServicesRepo.findByIdAndDeletedFlag(providedServiceId, Constants.NO);
            if (providedServicesOptional.isEmpty()) {
                return new ApiResponse<>("Provided Service not found", null, HttpStatus.NOT_FOUND.value());
            }
            ProvidedServices providedServices = providedServicesOptional.get();

            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setDescription(serviceRequestRequest.getDescription());
            serviceRequest.setBudget(serviceRequestRequest.getBudget());
            serviceRequest.setPreferredDate(serviceRequestRequest.getPreferredDate());
            setCoordinate(serviceRequest, serviceRequestRequest);
            setDisplayImages(serviceRequest, serviceRequestRequest);
            serviceRequest.setRequestStatus(RequestStatus.PENDING);
            serviceRequest.setProvidedServices(providedServices);
            serviceRequest.setUser(currentUser);
            ServiceRequest newServiceRequest = serviceRequestRepo.save(serviceRequest);
//            ServiceRequestResponse serviceRequestResponse = modelMapper.map(newServiceRequest, ServiceRequestResponse.class);
            return new ApiResponse<>("Success Making request", null, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error making a service request: ", e);
            return new ApiResponse<>("Error Making a request.", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    public ApiResponse<?> fetchAll() {
        try {
            List<ServiceRequest> serviceRequestList = serviceRequestRepo.findByDeletedFlag(Constants.NO);
            List<ServiceRequestResponse> serviceRequestResponseList = serviceRequestList
                    .stream()
                    .map(serviceRequest -> modelMapper
                            .map(serviceRequest, ServiceRequestResponse.class))
                    .toList();
            System.out.println(serviceRequestResponseList);
            return new ApiResponse<>("Success retrieving service request", serviceRequestResponseList, HttpStatus.OK.value());
        } catch (Exception e) {
            log.info("Error fetching all service requests:", e);
            return new ApiResponse<>("Error fetching all service requests", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> findByServiceProvider(Long serviceProviderId)
    {
        try {
            Optional<ProvidedServices> serviceProviderOptional = providedServicesRepo.findByIdAndDeletedFlag(serviceProviderId, Constants.NO);
            if (serviceProviderOptional.isEmpty())
            {
                return new ApiResponse<>("Service provider not found" , null , HttpStatus.NOT_FOUND.value());
            }
            ProvidedServices serviceProvider = serviceProviderOptional.get();
            List<ServiceRequest> serviceRequestList =  serviceRequestRepo.findByProvidedServices(serviceProvider);
            List<ServiceRequestResponse> serviceRequestResponses = serviceRequestList
                    .stream()
                    .map(serviceRequest -> modelMapper
                            .map(serviceProvider, ServiceRequestResponse.class))
                    .toList();
            return new ApiResponse<>("Success", serviceRequestResponses, HttpStatus.OK.value());
        } catch (Exception e)
        {
            log.info("Error", e);
            return null;
        }
    }

    public ApiResponse<?> getServiceRequestById(Long serviceRequestId) {
        try {
            Optional<ServiceRequest> serviceRequestOptional = serviceRequestRepo.findByIdAndDeletedFlag(serviceRequestId, Constants.NO);
            if (serviceRequestOptional.isEmpty()) {
                return new ApiResponse<>("Service request not found", null, HttpStatus.NOT_FOUND.value());
            }
            ServiceRequest serviceRequest = serviceRequestOptional.get();
            System.out.println("service request: " + serviceRequest);
//            ServiceRequestResponse  serviceRequestResponse = modelMapper.map(serviceRequest, ServiceRequestResponse.class);
            return new ApiResponse<>("Success retrieving a service request by Id", null, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error retrieving a service Request:", e);
            return new ApiResponse<>("Error retrieving a service request", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> updateServiceRequest(Long requestId, ServiceRequestRequest serviceRequestRequest) {
        try {
            Optional<ServiceRequest> serviceRequestOptional = serviceRequestRepo.findByIdAndDeletedFlag(requestId, Constants.NO);
            if (serviceRequestOptional.isEmpty()) {
                return new ApiResponse<>("Service Request Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            ServiceRequest serviceRequest = serviceRequestOptional.get();

            if (serviceRequestRequest.getPreferredDate() != null &&
                    !Objects.equals(serviceRequestRequest.getPreferredDate(), serviceRequest.getPreferredDate())) {
                serviceRequest.setPreferredDate(serviceRequestRequest.getPreferredDate());
            }
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceRequestRequest.getDescription(),
                    serviceRequest::setDescription, serviceRequest::getDescription);
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceRequestRequest.getBudget(),
                    serviceRequest::setBudget, serviceRequest::getBudget);
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceRequestRequest.getCoordinates().getLongitude(),
                    serviceRequest.getCoordinates()::setLongitude, serviceRequest.getCoordinates()::getLongitude);
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(serviceRequestRequest.getCoordinates().getLatitude(),
                    serviceRequest.getCoordinates()::setLatitude, serviceRequest.getCoordinates()::getLatitude);
            setDisplayImages(serviceRequest, serviceRequestRequest);
            ServiceRequest updatedServiceRequest = serviceRequestRepo.save(serviceRequest);
            ServiceRequestResponse serviceRequestResponse = modelMapper.map(updatedServiceRequest, ServiceRequestResponse.class);
            return new ApiResponse<>("Success Updating a service request", serviceRequestResponse, HttpStatus.OK.value());

        } catch (Exception e) {
            log.info("Error updating a service Request: ", e);
            return new ApiResponse<>("Error updating a service request", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public ApiResponse<?> deleteServiceRequest(Long serviceId) {
        try {
            Optional<ServiceRequest> serviceRequestOptional = serviceRequestRepo.findByIdAndDeletedFlag(serviceId, Constants.NO);
            if (serviceRequestOptional.isEmpty()) {
                return new ApiResponse<>("service request not found", null, HttpStatus.NOT_FOUND.value());
            }
            ServiceRequest serviceRequest = serviceRequestOptional.get();
            serviceRequest.setDeletedAt(LocalDateTime.now());
            serviceRequest.setDeletedBy(SecurityUtils.getCurrentUser());
            serviceRequest.setDeletedFlag(Constants.YES);
            serviceRequestRepo.save(serviceRequest);
            return new ApiResponse<>("Success deleting a service request", null, HttpStatus.NO_CONTENT.value());

        } catch (Exception e) {
            log.info("Error deleting a service request: ", e);
            return new ApiResponse<>("Error deleting a service request", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void setDisplayImages(ServiceRequest serviceRequest, ServiceRequestRequest serviceRequestRequest) {
        if (serviceRequestRequest.getDisplayImages() != null) {
            List<DisplayImagesRequest> displayImagesRequestList = serviceRequestRequest.getDisplayImages();
            List<DisplayImages> savedDisplayImages = displayImagesRequestList.stream()
                    .map(displayImages -> {
                        DisplayImages displayImage = new DisplayImages();
                        displayImage.setDisplayImage(displayImages.getDisplayImages());
                        return displayImagesRepository.save(displayImage);
                    })
                    .collect(Collectors.toList());
            serviceRequest.setDisplayImages(savedDisplayImages);
        }
    }

    private void setCoordinate(ServiceRequest serviceRequest, ServiceRequestRequest serviceRequestRequest) {
        if (serviceRequestRequest.getCoordinates() != null) {
            Cordinates coordinates = serviceRequest.getCoordinates();
            if (coordinates == null) {
                coordinates = new Cordinates();
            }
            coordinates.setLatitude(serviceRequestRequest.getCoordinates().getLatitude());
            coordinates.setLongitude(serviceRequestRequest.getCoordinates().getLongitude());
            serviceRequest.setCoordinates(coordinates);
        }
    }
}
