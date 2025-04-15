package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.model.BuyerRequest;
import edu.kingston.agriconnect.repository.BuyerRequestRepository;
import edu.kingston.agriconnect.repository.FarmerListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MarketplaceService {

    private BuyerRequestRepository buyerRequestRepository;

    private FarmerListingRepository farmerListingRepository;

    public BuyerRequest addBuyerRequest(BuyerRequest request) {
        return buyerRequestRepository.save(request);
    }


    public List<BuyerRequest> getAllRequests() {
        return buyerRequestRepository.findAll();
    }

    public BuyerRequest getBuyerRequest(Long id) {
        return buyerRequestRepository.findById(id).orElse(null);
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