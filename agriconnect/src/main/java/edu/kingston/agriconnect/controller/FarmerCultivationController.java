package edu.kingston.agriconnect.controller;


import edu.kingston.agriconnect.dto.FarmerCultivationDTO;
import edu.kingston.agriconnect.model.FarmerCultivation;
import edu.kingston.agriconnect.model.User;
import edu.kingston.agriconnect.repository.UserRepository;
import edu.kingston.agriconnect.service.FarmerCultivationService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/farmer/cultivations")
public class FarmerCultivationController {

    private final FarmerCultivationService cultivationService;

    private final UserRepository userRepository;

//    @PostMapping
//    public FarmerCultivation addCultivation(FarmerCultivation cultivation) {
//        return cultivationService.addCultivation(cultivation);
//    }

    @PostMapping
    public ResponseEntity<?> addCultivation(@RequestBody @Valid FarmerCultivationDTO dto) {
        User farmer = userRepository.findById(dto.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        FarmerCultivation cultivation = new FarmerCultivation();
        cultivation.setFarmer(farmer);
        cultivation.setCropType(dto.getCropType());
        cultivation.setCultivationDate(dto.getCultivationDate());
        cultivation.setHarvestDate(dto.getHarvestDate());
        cultivation.setLocation(dto.getLocation());
        cultivation.setLandSize(dto.getLandSize());
        cultivation.setExpectedYield(dto.getExpectedYield());
        cultivation.setMethodOfCultivation(dto.getMethodOfCultivation());
        cultivation.setDescription(dto.getDescription());

        FarmerCultivation savedCultivation = cultivationService.addCultivation(cultivation);
        return ResponseEntity.ok(savedCultivation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmerCultivation> updateCultivation(@PathVariable("id") Long id, @RequestBody @Valid FarmerCultivationDTO dto) {
        try {
            FarmerCultivation updatedCultivation = cultivationService.update(id, dto);
            return ResponseEntity.ok(updatedCultivation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<FarmerCultivation> getAllCultivations() {
        return cultivationService.getAllCultivations();
    }

    @GetMapping("/{farmerId}")
    public List<FarmerCultivation> getCultivationsByFarmer(@PathVariable("farmerId") Long farmerId) {
        return cultivationService.getCultivationsByFarmer(farmerId);
    }
}
