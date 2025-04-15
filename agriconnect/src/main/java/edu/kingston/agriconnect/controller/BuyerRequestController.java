package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.BuyerRequestDTO;
import edu.kingston.agriconnect.model.BuyerRequest;
import edu.kingston.agriconnect.service.BuyerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/buyer/requests")
public class BuyerRequestController {

    private final BuyerRequestService buyerRequestService;

    @PostMapping
    public BuyerRequestDTO addBuyerRequest(@RequestBody BuyerRequestDTO dto) {
        return buyerRequestService.addBuyerRequest(dto);
    }

    @PutMapping("/{id}")
    public BuyerRequestDTO updateBuyerRequest(@PathVariable Long id, @RequestBody BuyerRequestDTO dto) {
        return buyerRequestService.updateBuyerRequest(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBuyerRequest(@PathVariable Long id) {
        buyerRequestService.deleteBuyerRequest(id);
        return ResponseEntity.ok("Buyer request deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<BuyerRequestDTO>> getAllRequests() {
        List<BuyerRequestDTO> buyerRequests = buyerRequestService.getAllRequests()
                .stream()
                .map(this::mapToDTO).toList();
        return ResponseEntity.ok(buyerRequests);
    }

    @GetMapping("/{id}")
    public BuyerRequestDTO getBuyerRequest(@PathVariable Long id) {
        BuyerRequest buyerRequest = buyerRequestService.getBuyerRequest(id);
        return mapToDTO(buyerRequest);
    }

    @GetMapping("buyer-id/{buyerId}")
    public List<BuyerRequestDTO> getBuyerRequestsByBuyer(@PathVariable Long buyerId) {
        return buyerRequestService.getBuyerRequestsByBuyer(buyerId);
    }

    private BuyerRequestDTO mapToDTO(BuyerRequest buyerRequests) {
        BuyerRequestDTO dto = new BuyerRequestDTO();
        dto.setId(buyerRequests.getId());
        dto.setBuyerId(buyerRequests.getBuyer().getId());
        dto.setCropType(buyerRequests.getCropType());
        dto.setQuantity(buyerRequests.getQuantity());
        dto.setQuantityType(buyerRequests.getQuantityType());
        dto.setLocation(buyerRequests.getLocation());
        dto.setStartDate(buyerRequests.getStartDate());
        dto.setDeadline(buyerRequests.getDeadline());
        dto.setStatus(buyerRequests.getStatus());
        dto.setRequestType(buyerRequests.getRequestType());
        dto.setDescription(buyerRequests.getDescription());
        dto.setRating(buyerRequests.getRating());
        dto.setInWishlist(buyerRequests.getInWishlist());
        return dto;
    }
}
