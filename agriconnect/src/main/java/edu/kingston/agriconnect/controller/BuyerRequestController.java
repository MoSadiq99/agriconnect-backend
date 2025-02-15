package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.model.BuyerRequest;
import edu.kingston.agriconnect.service.BuyerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/buyer/requests")
public class BuyerRequestController {

    private final BuyerRequestService buyerRequestService;

    @PostMapping
    public BuyerRequest addBuyerRequest(@RequestBody BuyerRequest request) {
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }
        return buyerRequestService.addBuyerRequest(request);
    }

    @GetMapping
    public List<BuyerRequest> getAllRequests() {
        return buyerRequestService.getAllRequests();
    }

    @GetMapping("/{id}")
    public BuyerRequest getBuyerRequest(Long id) {
        return buyerRequestService.getBuyerRequest(id);
    }
}
