package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.FarmerCultivationDTO;
import edu.kingston.agriconnect.service.FarmerCultivationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/farmer/cultivations")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FarmerCultivationController {

    private final FarmerCultivationService cultivationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FarmerCultivationDTO addCultivation(@RequestBody @Valid FarmerCultivationDTO dto) {
        return cultivationService.addCultivation(dto);
    }

    @PutMapping("/{id}")
    public FarmerCultivationDTO updateCultivation(@PathVariable("id") Long id, @RequestBody @Valid FarmerCultivationDTO dto) {
        return cultivationService.updateCultivation(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCultivation(@PathVariable("id") Long id) {
        cultivationService.deleteCultivation(id);
    }

    @GetMapping
    public List<FarmerCultivationDTO> getAllCultivations() {
        return cultivationService.getAllCultivations();
    }

    @GetMapping("/page")
    public Page<FarmerCultivationDTO> getAllCultivationsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return cultivationService.getAllCultivationsPage(PageRequest.of(page, size));
    }

    @GetMapping("/{farmerId}")
    public List<FarmerCultivationDTO> getCultivationsByFarmer(@PathVariable("farmerId") Long farmerId) {
        return cultivationService.getCultivationsByFarmer(farmerId);
    }
}
