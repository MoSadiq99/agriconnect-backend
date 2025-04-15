package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.FarmerListingDTO;
import edu.kingston.agriconnect.model.FarmerListing;
import edu.kingston.agriconnect.service.FarmerListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmer/listings")
@RequiredArgsConstructor
@Validated
public class FarmerListingController {

    private static final Logger logger = LoggerFactory.getLogger(FarmerListingController.class);

    private final FarmerListingService farmerListingService;

    @GetMapping
    public ResponseEntity<List<FarmerListingDTO>> getAllListings() {
        logger.info("Received request to fetch all farmer listings");
        List<FarmerListingDTO> listings = farmerListingService.getAllListings()
                .stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(listings);
    }

    @GetMapping(params = "farmerId")
    public ResponseEntity<List<FarmerListingDTO>> getListingsByFarmer(@RequestParam("farmerId") Long farmerId) {
        logger.info("Received request to fetch listings by farmer ID: {}", farmerId);
        try {
            List<FarmerListingDTO> listings = farmerListingService.getAllListings()
                    .stream()
                    .filter(listing -> listing.getFarmer().getId().equals(farmerId))
                    .map(this::mapToDTO)
                    .toList();
            return ResponseEntity.ok(listings);
        } catch (Exception e) {
            // Log the error
            System.err.println("Error fetching listings: " + e.getMessage());
            // Return empty list in case of error to match the TypeScript behavior
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmerListingDTO> getFarmerListing(@PathVariable Long id) {
        logger.info("Received request to fetch farmer listing with ID: {}", id);
        FarmerListing listing = farmerListingService.getFarmerListing(id);
        return ResponseEntity.ok(mapToDTO(listing));
    }

    @GetMapping("/match/{cropType}")
    public ResponseEntity<List<FarmerListingDTO>> findMatches(@PathVariable String cropType) {
        logger.info("Received request to find listings matching crop type: {}", cropType);
        List<FarmerListingDTO> matches = farmerListingService.findMatches(cropType)
                .stream()
                .map(this::mapToDTO)
                .toList();
        return ResponseEntity.ok(matches);
    }

    @PostMapping
    public ResponseEntity<FarmerListingDTO> addFarmerListing(@Valid @RequestBody FarmerListingDTO dto) {
        logger.info("Received request to add farmer listing");
        FarmerListingDTO savedListing = farmerListingService.addFarmerListing(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedListing);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmerListingDTO> updateFarmerListing(@PathVariable Long id, @Valid @RequestBody FarmerListingDTO dto) {
        logger.info("Received request to update farmer listing with ID: {}", id);
        FarmerListingDTO updatedListing = farmerListingService.updateFarmerListing(id, dto);
        return ResponseEntity.ok(updatedListing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFarmerListing(@PathVariable Long id) {
        logger.info("Received request to delete farmer listing with ID: {}", id);
        farmerListingService.deleteFarmerListing(id);
        return ResponseEntity.ok("Farmer listing deleted successfully");
    }

    // Reusable method to map entity to DTO (duplicated here for controller consistency)
    private FarmerListingDTO mapToDTO(FarmerListing listing) {
        FarmerListingDTO dto = new FarmerListingDTO();
        dto.setId(listing.getId());
        dto.setFarmerId(listing.getFarmer().getId());
        dto.setCultivationId(listing.getCultivation().getId());
        dto.setCropType(listing.getCropType());
        dto.setQuantity(listing.getQuantity());
        dto.setUnit(listing.getUnit());
        dto.setLocation(listing.getLocation());
        dto.setPrice(listing.getPrice());
        dto.setDescription(listing.getDescription());
        dto.setAvailableDateFrom(listing.getAvailableDateFrom());
        dto.setStatus(listing.getStatus());
        dto.setMethodOfCultivation(listing.getMethodOfCultivation());
        return dto;
    }
}