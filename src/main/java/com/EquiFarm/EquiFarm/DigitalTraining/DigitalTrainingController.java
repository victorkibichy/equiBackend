package com.EquiFarm.EquiFarm.DigitalTraining;

import com.EquiFarm.EquiFarm.DigitalTraining.DTO.DigitalTrainingUserRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.DTO.ExpertiseRequest;
import com.EquiFarm.EquiFarm.ServiceProvider.ServiceProviderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/digitalTraining")
@Tag(name = "Digital Trainings")
@RequiredArgsConstructor
public class DigitalTrainingController {
    private final DigitalTrainingService digitalTrainingService;

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllDigitalTrainings() {
        return ResponseEntity.ok(digitalTrainingService.getAllDigitalTrainings());
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createNewDigitalTraining(@RequestBody DigitalTrainingUserRequest digitalTraining){
        return ResponseEntity.ok(digitalTrainingService.createDigitalTraining(digitalTraining));
    }

    @GetMapping("/get/by/id/{digitalTrainingId}")
    public ResponseEntity<?> getById(@PathVariable("digitalTrainingId") Long digitalTrainingId){
        return ResponseEntity.ok(digitalTrainingService.getDigitalTrainingById(digitalTrainingId));
    }
    @PutMapping("/update/{digitalTrainingId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateDigitalTraining(@PathVariable("digitalTrainingId") Long digitalTrainingId,
                                             @RequestBody DigitalTrainingUserRequest digitalTrainingUserRequest){
        return ResponseEntity.ok(digitalTrainingService.updateDigitalTraining(digitalTrainingId, digitalTrainingUserRequest));
    }

    @DeleteMapping("/delete/{digitalTrainingId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteDigitalTraining(@PathVariable("digitalTrainingId") Long digitalTrainingId){
        return ResponseEntity.ok(digitalTrainingService.deleteDigitalTraining(digitalTrainingId));
    }


}
