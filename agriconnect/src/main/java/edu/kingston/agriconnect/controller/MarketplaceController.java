package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.model.BuyerRequest;
import edu.kingston.agriconnect.model.FarmerListing;
import edu.kingston.agriconnect.service.MarketplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/marketplace")
public class MarketplaceController {

    @Autowired
    private MarketplaceService marketplaceService;

//    @PostMapping("/buyer/requests")
//    public BuyerRequest addBuyerRequest(@RequestBody BuyerRequest request) {
//        return marketplaceService.addBuyerRequest(request);
//    }
    @PostMapping("/buyer/requests")
    public BuyerRequest addBuyerRequest(@RequestBody BuyerRequest request) {
        // Add validation for startDate (e.g., must be in the future)
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }
        return marketplaceService.addBuyerRequest(request);
    }


    @PostMapping("/farmer/listings")
    public FarmerListing addFarmerListing(@RequestBody FarmerListing listing) {
        return marketplaceService.addFarmerListing(listing);
    }

    @GetMapping("/match/{cropType}")
    public List<FarmerListing> findMatches(@PathVariable String cropType) {
        return marketplaceService.findMatches(cropType);
    }

}