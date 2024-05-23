package com.EquiFarm.EquiFarm.ValueChain;

import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainRequest;
import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainResponse;
import com.EquiFarm.EquiFarm.config.SecurityUtils;
import com.EquiFarm.EquiFarm.utils.ApiResponse;
import com.EquiFarm.EquiFarm.utils.Constants;
import com.EquiFarm.EquiFarm.utils.FieldUpdateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerTypePredicate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValueChainService {
    private final ValueChainRepo valueChainRepo;
    private final ModelMapper modelMapper;

    public ApiResponse<?> createValueChain(ValueChainRequest valueChainRequest){
        try {
            ValueChain valueChain = new ValueChain();
            valueChain.setValueChain(valueChainRequest.getValueChain());
            valueChain.setDescription(valueChainRequest.getDescription());
            ValueChain valueChain1 = valueChainRepo.save(valueChain);
            ValueChainResponse valueChainResponse = modelMapper.map(valueChain1, ValueChainResponse.class);
            return new ApiResponse<>("Success adding value chain", valueChainResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error creating a value chain", e);
            return new ApiResponse<>("Error creating a value chain", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getAll(){
        try {
            List<ValueChain> valueChainList = valueChainRepo.findByDeletedFlag(Constants.NO);
            List<ValueChainResponse> valueChainResponses = valueChainList
                    .stream()
                    .map(valueChain -> modelMapper
                            .map(valueChain, ValueChainResponse.class))
                    .toList();
            return new ApiResponse<>("Success fetching value chains", valueChainResponses, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error fetching all value chains", e);
            return new ApiResponse<>("Error fetching all value chains", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> getById(Long valueChainId){
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()){
                return new ApiResponse<>("Value Chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            ValueChainResponse valueChainResponse = modelMapper.map(valueChain, ValueChainResponse.class);
            return new ApiResponse<>("Success fetching value chain by id", valueChainResponse, HttpStatus.OK.value());

        } catch (Exception e){
            log.info("Error fetching by id", e);
            return new ApiResponse<>("Error fetching value chain by id", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> updateValueChain(Long valueChainId, ValueChainRequest valueChainRequest){
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()){
                return new ApiResponse<>("Value Chain not found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();

            FieldUpdateUtil.updateFieldIfNotNullAndChanged(valueChainRequest.getValueChain(),
                    valueChain::setValueChain, valueChain::getValueChain);
            FieldUpdateUtil.updateFieldIfNotNullAndChanged(valueChainRequest.getDescription(),
                    valueChain::setDescription, valueChain::getDescription);
            ValueChain updatedValueChain = valueChainRepo.save(valueChain);
            ValueChainResponse valueChainResponse = modelMapper.map(updatedValueChain, ValueChainResponse.class);
            return new ApiResponse<>("Success updating value chain", valueChainResponse, HttpStatus.OK.value());
        } catch (Exception e){
            log.info("Error updating a value chain", e);
            return new ApiResponse<>("Error updating a value chain", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public ApiResponse<?> deleteValueChain(Long valueChainId){
        try {
            Optional<ValueChain> valueChainOptional = valueChainRepo.findByIdAndDeletedFlag(valueChainId, Constants.NO);
            if (valueChainOptional.isEmpty()){
                return new ApiResponse<>("Value Chain Not Found", null, HttpStatus.NOT_FOUND.value());
            }
            ValueChain valueChain = valueChainOptional.get();
            valueChain.setDeletedAt(LocalDateTime.now());
            valueChain.setDeletedBy(SecurityUtils.getCurrentUser());
            valueChain.setDeletedFlag(Constants.YES);
            valueChainRepo.save(valueChain);
            return new ApiResponse<>("Success deleting a value chain", null, HttpStatus.NO_CONTENT.value());
        } catch (Exception e){
            log.info("Error deleting a value chain", e);
            return new ApiResponse<>("Error deleting a value chain", null, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
