package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.model.FarmerListing;
import edu.kingston.agriconnect.service.FarmerListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/farmer/listings")
public class FarmerListingController {

    private final FarmerListingService farmerListingService;

    @PostMapping
    public FarmerListing addFarmerListing(@RequestBody FarmerListing listing) {
        return farmerListingService.addFarmerListing(listing);
    }

    @GetMapping
    public List<FarmerListing> getAllListings() {
        return farmerListingService.getAllListings();
    }

    @GetMapping("/{id}")
    public FarmerListing getFarmerListing(@PathVariable Long id) {
        return farmerListingService.getFarmerListing(id);
    }

    @GetMapping("/match/{cropType}")
    public List<FarmerListing> findMatches(@PathVariable String cropType) {
        return farmerListingService.findMatches(cropType);
    }
}
