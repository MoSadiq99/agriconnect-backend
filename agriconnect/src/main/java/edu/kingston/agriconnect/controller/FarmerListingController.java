package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.FarmerListingDTO;
import edu.kingston.agriconnect.service.FarmerListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
        List<FarmerListingDTO> listings = farmerListingService.getAllListings();
        return ResponseEntity.ok(listings);
    }

    @GetMapping(params = "farmerId")
    public ResponseEntity<List<FarmerListingDTO>> getListingsByFarmer(@RequestParam("farmerId") Long farmerId) {
        try {
            List<FarmerListingDTO> listings = farmerListingService.getAllListings();
            return ResponseEntity.ok(listings);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmerListingDTO> getFarmerListing(@PathVariable Long id) {
        logger.info("Received request to fetch farmer listing with ID: {}", id);
        FarmerListingDTO listing = farmerListingService.getFarmerListing(id);
        return ResponseEntity.ok(listing);
    }

    @GetMapping("/match/{cropType}")
    public ResponseEntity<List<FarmerListingDTO>> findMatches(@PathVariable String cropType) {
        logger.info("Received request to find listings matching crop type: {}", cropType);
        List<FarmerListingDTO> matches = farmerListingService.findMatches(cropType);
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
}