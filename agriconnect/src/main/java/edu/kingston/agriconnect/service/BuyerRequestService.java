package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.model.BuyerRequest;
import edu.kingston.agriconnect.repository.BuyerRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyerRequestService {

    private final BuyerRequestRepository buyerRequestRepository;


    public BuyerRequest addBuyerRequest(BuyerRequest request) {
        return buyerRequestRepository.save(request);
    }

    public List<BuyerRequest> getAllRequests() {
        return buyerRequestRepository.findAll();
    }

    public BuyerRequest getBuyerRequest(Long id) {
        return buyerRequestRepository.findById(id).orElse(null);
    }

}
