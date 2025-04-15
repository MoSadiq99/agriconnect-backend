package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.FarmerListingDTO;
import edu.kingston.agriconnect.exception.BusinessException;
import edu.kingston.agriconnect.model.BuyerRequest;
import edu.kingston.agriconnect.model.FarmerCultivation;
import edu.kingston.agriconnect.model.FarmerListing;
import edu.kingston.agriconnect.model.User;
import edu.kingston.agriconnect.model.enums.RequestType;
import edu.kingston.agriconnect.repository.FarmerCultivationRepository;
import edu.kingston.agriconnect.repository.FarmerListingRepository;
import edu.kingston.agriconnect.repository.FarmerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static edu.kingston.agriconnect.exception.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FarmerListingService {

    private static final Logger logger = LoggerFactory.getLogger(FarmerListingService.class);

    private final FarmerListingRepository farmerListingRepository;
    private final FarmerRepository farmerRepository;
    private final FarmerCultivationRepository farmerCultivationRepository;

    // Add a new farmer listing
    public FarmerListingDTO addFarmerListing(FarmerListingDTO dto) {
        logger.info("Adding farmer listing for farmer ID: {}", dto.getFarmerId());
        User farmer = farmerRepository.findById(dto.getFarmerId())
                .orElseThrow(() -> new BusinessException(FARMER_NOT_FOUND, "Farmer not found with ID: " + dto.getFarmerId()));
        FarmerCultivation cultivation = farmerCultivationRepository.findById(dto.getCultivationId())
                .orElseThrow(() -> new BusinessException(CULTIVATION_NOT_FOUND, "Cultivation not found with ID: " + dto.getCultivationId()));

        FarmerListing listing = mapToEntity(dto, farmer, cultivation);
        FarmerListing savedListing = farmerListingRepository.save(listing);
        logger.debug("Saved farmer listing with ID: {}", savedListing.getId());
        return mapToDTO(savedListing);
    }

    // Get a single farmer listing by ID
    public FarmerListing getFarmerListing(Long id) {
        logger.info("Fetching farmer listing with ID: {}", id);
        return farmerListingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FARMER_LISTING_NOT_FOUND, "Farmer listing not found with ID: " + id));
    }

    // Find listings by crop type
    public List<FarmerListing> findMatches(String cropType) {
        logger.info("Finding listings for crop type: {}", cropType);
        return farmerListingRepository.findByCropTypeIgnoreCase(cropType);
    }

    // Find listings matching a buyer request
    public List<FarmerListing> findMatches(BuyerRequest request) {
        logger.info("Finding listings matching buyer request for crop type: {}", request.getCropType());
        List<FarmerListing> allListings = farmerListingRepository.findByCropTypeIgnoreCase(request.getCropType());
        return allListings.stream()
                .filter(listing -> matchesQuantity(listing, request))
                .toList();
    }

    // Get all farmer listings
    public List<FarmerListing> getAllListings() {
        logger.info("Fetching all farmer listings");
        return farmerListingRepository.findAll();
    }

    // Update an existing farmer listing with null checks
    public FarmerListingDTO updateFarmerListing(Long id, FarmerListingDTO dto) {
        logger.info("Updating farmer listing with ID: {}", id);
        FarmerListing existingListing = farmerListingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FARMER_LISTING_NOT_FOUND, "Farmer listing not found with ID: " + id));

        updateNonNullFields(existingListing, dto);
        FarmerListing updatedListing = farmerListingRepository.save(existingListing);
        logger.debug("Updated farmer listing with ID: {}", updatedListing.getId());
        return mapToDTO(updatedListing);
    }

    // Delete a farmer listing
    public void deleteFarmerListing(Long id) {
        logger.info("Deleting farmer listing with ID: {}", id);
        if (!farmerListingRepository.existsById(id)) {
            throw new BusinessException(FARMER_LISTING_NOT_FOUND, "Farmer listing not found with ID: " + id);
        }
        farmerListingRepository.deleteById(id);
    }

    // Reusable method to map DTO to entity
    private FarmerListing mapToEntity(FarmerListingDTO dto, User farmer, FarmerCultivation cultivation) {
        FarmerListing listing = new FarmerListing();
        listing.setCropType(dto.getCropType());
        listing.setQuantity(dto.getQuantity());
        listing.setUnit(dto.getUnit());
        listing.setLocation(dto.getLocation());
        listing.setPrice(dto.getPrice());
        listing.setDescription(dto.getDescription());
        listing.setAvailableDateFrom(dto.getAvailableDateFrom());
        listing.setFarmer(farmer);
        listing.setCultivation(cultivation);
        listing.setStatus(dto.getStatus());
        listing.setMethodOfCultivation(dto.getMethodOfCultivation());
        return listing;
    }

    // Reusable method to map entity to DTO
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

    // Reusable method to update only non-null fields
    private void updateNonNullFields(FarmerListing listing, FarmerListingDTO dto) {
        if (dto.getCropType() != null) listing.setCropType(dto.getCropType());
        if (dto.getQuantity() != null) listing.setQuantity(dto.getQuantity());
        if (dto.getUnit() != null) listing.setUnit(dto.getUnit());
        if (dto.getLocation() != null) listing.setLocation(dto.getLocation());
        if (dto.getPrice() != null) listing.setPrice(dto.getPrice());
        if (dto.getDescription() != null) listing.setDescription(dto.getDescription());
        if (dto.getAvailableDateFrom() != null) listing.setAvailableDateFrom(dto.getAvailableDateFrom());
        if (dto.getStatus() != null) listing.setStatus(dto.getStatus());
        if (dto.getMethodOfCultivation() != null) listing.setMethodOfCultivation(dto.getMethodOfCultivation());
    }

    // Reusable method to check quantity matching logic
    private boolean matchesQuantity(FarmerListing listing, BuyerRequest request) {
        int availableQuantity = Integer.parseInt(listing.getQuantity());
        if (request.getRequestType() == RequestType.DAILY_AVERAGE) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), request.getDeadline());
            return days > 0 && availableQuantity >= request.getQuantity() / days;
        } else {
            return availableQuantity >= request.getQuantity();
        }
    }

    @Transactional
    public void deleteAllByFarmerId(Long id) {
        farmerListingRepository.deleteAllByFarmerId(id);
    }
}