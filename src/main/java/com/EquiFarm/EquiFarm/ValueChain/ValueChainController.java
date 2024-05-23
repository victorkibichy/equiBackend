package com.EquiFarm.EquiFarm.ValueChain;

import com.EquiFarm.EquiFarm.ValueChain.DTO.ValueChainRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/value/chain")
@Tag(name = "Value Chains")
@RequiredArgsConstructor
public class ValueChainController {
    private final ValueChainService valueChainService;
    @PostMapping("/new")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<?> createValueChain(@RequestBody ValueChainRequest valueChainRequest){
        return ResponseEntity.ok(valueChainService.createValueChain(valueChainRequest));
    }
    @GetMapping("/get/all")
    public ResponseEntity<?> getAll(){
       return ResponseEntity.ok(valueChainService.getAll());
    }

    @GetMapping("/get/by/id/{valueChainId}")
    public ResponseEntity<?> fetchById(@PathVariable("valueChainId") Long valueChainId){
        return ResponseEntity.ok(valueChainService.getById(valueChainId));
    }

    @PutMapping("/update/{valueChainId}")
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<?> updateValueChain(@PathVariable("valueChainId") Long valueChainId,
                                              @RequestBody ValueChainRequest valueChainRequest){
        return ResponseEntity.ok(valueChainService.updateValueChain(valueChainId, valueChainRequest));
    }

    @DeleteMapping("/delete/{valueChainId}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<?> deleteValueChain(@PathVariable("valueChainId") Long valueChainId){
        return ResponseEntity.ok(valueChainService.deleteValueChain(valueChainId));
    }
}
