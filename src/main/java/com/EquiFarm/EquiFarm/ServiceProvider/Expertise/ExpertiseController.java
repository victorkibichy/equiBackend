//package com.EquiFarm.EquiFarm.ServiceProvider.Expertise;
//
//import com.EquiFarm.EquiFarm.ServiceProvider.Expertise.DTO.ExpertiseRequest;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/serviceProvider/expertise")
//@Tag(name = "Service Provider Expertise")
//@RequiredArgsConstructor
//public class ExpertiseController {
//    private final ExpertiseService expertiseService;
//
//    @PostMapping("/new")
//    @PreAuthorize("hasAuthority('admin:create')")
//    public ResponseEntity<?> createNewExpertise(@RequestBody ExpertiseRequest expertise){
//        return ResponseEntity.ok(expertiseService.createExpertise(expertise));
//    }
//
//    @GetMapping("get/all")
//    public ResponseEntity<?> fetchAll(){
//        return ResponseEntity.ok(expertiseService.fetchAll());
//    }
//
//    @GetMapping("/get/by/id/{expertiseId}")
//    public ResponseEntity<?> getById(@PathVariable("expertiseId") Long expertId){
//        return ResponseEntity.ok(expertiseService.getExpertiseById(expertId));
//    }
//
//    @PutMapping("/update/{expertiseId}")
//    @PreAuthorize("hasAuthority('admin:update')")
//    public ResponseEntity<?> updateExpertise(@PathVariable("expertiseId") Long expertiseId,
//                                             @RequestBody ExpertiseRequest expertiseRequest){
//        return ResponseEntity.ok(expertiseService.updateExpertise(expertiseId, expertiseRequest));
//    }
//
//    @DeleteMapping("/delete/{expertiseId}")
//    @PreAuthorize("hasAuthority('admin:delete')")
//    public ResponseEntity<?> deleteExpertise(@PathVariable("expertiseId") Long expertiseId){
//        return ResponseEntity.ok(expertiseService.deleteExpertise(expertiseId));
//    }
//}
