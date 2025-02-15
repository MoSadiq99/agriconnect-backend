package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.model.BuyerRequest;
import edu.kingston.agriconnect.model.FarmerListing;
import edu.kingston.agriconnect.model.enums.RequestType;
import edu.kingston.agriconnect.repository.FarmerListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FarmerListingService {
    private final FarmerListingRepository farmerListingRepository;

    public FarmerListing addFarmerListing(FarmerListing listing) {
        return farmerListingRepository.save(listing);
    }

    public List<FarmerListing> findMatches(String cropType) {
        return farmerListingRepository.findAll()
                .stream()
                .filter(listing -> listing.getCropType().equalsIgnoreCase(cropType))
                .toList();
    }

    public List<FarmerListing> findMatches(BuyerRequest request) {
        List<FarmerListing> allListings = farmerListingRepository.findAll();

        return allListings.stream()
                .filter(listing -> listing.getCropType().equalsIgnoreCase(request.getCropType()))
                .filter(listing -> {
                    if (request.getRequestType() == RequestType.DAILY_AVERAGE) {
                        // Match based on daily average availability
                        return listing.getQuantity() >= request.getQuantity() / ChronoUnit.DAYS.between(LocalDate.now(), request.getDeadline());
                    } else {
                        // Match based on total quantity for one-time requirement
                        return listing.getQuantity() >= request.getQuantity();
                    }
                })
                .toList();
    }

    public FarmerListing getFarmerListing(Long id) {
        return farmerListingRepository.findById(id).orElse(null);
    }

    public List<FarmerListing> getAllListings() {
        return farmerListingRepository.findAll();
    }

    //    public List<FarmerListing> findMatches(BuyerRequest request) {
//        return farmerListingRepository.findAll()
//                .stream()
//                .filter(listing -> listing.getCropType().equalsIgnoreCase(request.getCropType()))
//                .filter(listing -> {
//                    boolean dateOverlap = !listing.getAvailableDateFrom().isBefore(request.getStartDate()) &&
//                            !listing.getAvailableDateFrom().isAfter(request.getDeadline());
//                    return dateOverlap && listing.getQuantity() >= request.getQuantity();
//                })
//                .toList();
//    }
}
