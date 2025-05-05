package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.BuyerRequestDTO;
import edu.kingston.agriconnect.exception.BusinessErrorCodes;
import edu.kingston.agriconnect.exception.BusinessException;
import edu.kingston.agriconnect.mapper.BuyerRequestMapper;
import edu.kingston.agriconnect.model.BuyerRequest;
import edu.kingston.agriconnect.model.User;
import edu.kingston.agriconnect.repository.BuyerRequestRepository;
import edu.kingston.agriconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyerRequestService {

    private static final Logger logger = LoggerFactory.getLogger(BuyerRequestService.class);
    private final BuyerRequestRepository buyerRequestRepository;
    private final UserRepository userRepository;
    private final BuyerRequestMapper mapper;

    public BuyerRequestDTO addBuyerRequest(BuyerRequestDTO dto) {
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }
        User buyer = userRepository.findById(dto.getBuyerId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.BUYER_NOT_FOUND));

        BuyerRequest buyerRequest = mapper.toEntity(dto);
        buyerRequest.setBuyer(buyer);
        BuyerRequest savedBuyerRequest = buyerRequestRepository.save(buyerRequest);
        return mapper.toDto(savedBuyerRequest);
    }

    private BuyerRequestDTO mapToDTO(BuyerRequest savedBuyerRequest) {
        BuyerRequestDTO dto = new BuyerRequestDTO();
        dto.setId(savedBuyerRequest.getId());
        dto.setBuyerId(savedBuyerRequest.getBuyer().getId());
        dto.setCropType(savedBuyerRequest.getCropType());
        dto.setQuantity(savedBuyerRequest.getQuantity());
        dto.setQuantityType(savedBuyerRequest.getQuantityType());
        dto.setLocation(savedBuyerRequest.getLocation());
        dto.setStartDate(savedBuyerRequest.getStartDate());
        dto.setDeadline(savedBuyerRequest.getDeadline());
        dto.setStatus(savedBuyerRequest.getStatus());
        dto.setRequestType(savedBuyerRequest.getRequestType());
        dto.setDescription(savedBuyerRequest.getDescription());
        dto.setRating(savedBuyerRequest.getRating());
        dto.setInWishlist(savedBuyerRequest.getInWishlist());
        return dto;
    }

    private BuyerRequest mapToEntity(BuyerRequestDTO dto, User buyer) {
        BuyerRequest buyerRequest = new BuyerRequest();
        buyerRequest.setBuyer(buyer);
        buyerRequest.setCropType(dto.getCropType());
        buyerRequest.setQuantity(dto.getQuantity());
        buyerRequest.setQuantityType(dto.getQuantityType());
        buyerRequest.setLocation(dto.getLocation());
        buyerRequest.setStartDate(dto.getStartDate());
        buyerRequest.setDeadline(dto.getDeadline());
        buyerRequest.setStatus(dto.getStatus());
        buyerRequest.setRequestType(dto.getRequestType());
        buyerRequest.setDescription(dto.getDescription());
        buyerRequest.setRating(dto.getRating());
        buyerRequest.setInWishlist(dto.getInWishlist());
        buyerRequest.setCreatedAt(LocalDateTime.now());
        return buyerRequestRepository.save(buyerRequest);
    }

    public List<BuyerRequest> getAllRequests() {
        return buyerRequestRepository.findAll();
    }

    public BuyerRequest getBuyerRequest(Long id) {
        return buyerRequestRepository.findById(id).orElse(null);
    }

    public BuyerRequestDTO updateBuyerRequest(Long id, BuyerRequestDTO dto) {
       BuyerRequest existingBuyerRequest = buyerRequestRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Buyer request not found"));

       updateNonNullFields(existingBuyerRequest, dto);
       BuyerRequest updatedBuyerRequest = buyerRequestRepository.save(existingBuyerRequest);
       return mapToDTO(updatedBuyerRequest);
    }

    private void updateNonNullFields(BuyerRequest existingBuyerRequest, BuyerRequestDTO dto) {
        if (dto.getCropType() != null) existingBuyerRequest.setCropType(dto.getCropType());
        if (dto.getQuantity() != null) existingBuyerRequest.setQuantity(dto.getQuantity());
        if (dto.getQuantityType() != null) existingBuyerRequest.setQuantityType(dto.getQuantityType());
        if (dto.getLocation() != null) existingBuyerRequest.setLocation(dto.getLocation());
        if (dto.getStartDate() != null) existingBuyerRequest.setStartDate(dto.getStartDate());
        if (dto.getDeadline() != null) existingBuyerRequest.setDeadline(dto.getDeadline());
        if (dto.getStatus() != null) existingBuyerRequest.setStatus(dto.getStatus());
        if (dto.getRequestType() != null) existingBuyerRequest.setRequestType(dto.getRequestType());
        if (dto.getDescription() != null) existingBuyerRequest.setDescription(dto.getDescription());
        if (dto.getRating() != null) existingBuyerRequest.setRating(dto.getRating());
        if (dto.getInWishlist() != null) existingBuyerRequest.setInWishlist(dto.getInWishlist());
        existingBuyerRequest.setUpdatedAt(LocalDateTime.now());
    }

    public void deleteBuyerRequest(Long id) {
        logger.info("Deleting buyer request with ID: {}", id);
        buyerRequestRepository.deleteById(id);
    }

    public List<BuyerRequestDTO> getBuyerRequestsByBuyer(Long buyerId) {
        return buyerRequestRepository.findByBuyerId(buyerId)
                .stream()
                .map(this::mapToDTO).toList();
    }

    public void deleteAllByBuyerId(Long id) {
        buyerRequestRepository.deleteAllByBuyerId(id);
    }
}
