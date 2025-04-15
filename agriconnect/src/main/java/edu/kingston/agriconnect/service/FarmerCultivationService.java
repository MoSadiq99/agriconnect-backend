package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.FarmerCultivationDTO;
import edu.kingston.agriconnect.exception.BusinessErrorCodes;
import edu.kingston.agriconnect.exception.BusinessException;
import edu.kingston.agriconnect.mapper.FarmerCultivationMapper;
import edu.kingston.agriconnect.model.FarmerCultivation;
import edu.kingston.agriconnect.model.User;
import edu.kingston.agriconnect.repository.FarmerCultivationRepository;
import edu.kingston.agriconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmerCultivationService {

    private final FarmerCultivationRepository cultivationRepository;
    private final UserRepository userRepository;
    private final FarmerCultivationMapper mapper;

    @Transactional
    public FarmerCultivationDTO addCultivation(FarmerCultivationDTO dto) {
        User farmer = userRepository.findById(dto.getFarmerId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.FARMER_NOT_FOUND));
        FarmerCultivation cultivation = mapper.toEntity(dto);
        cultivation.setFarmer(farmer);
        FarmerCultivation saved = cultivationRepository.save(cultivation);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<FarmerCultivationDTO> getCultivationsByFarmer(Long farmerId) {
        List<FarmerCultivation> cultivations = cultivationRepository.findByFarmerId(farmerId);
        if (cultivations.isEmpty() && !userRepository.existsById(farmerId)) {
            throw new BusinessException(BusinessErrorCodes.FARMER_NOT_FOUND);
        }
        return mapper.toDtoList(cultivations);
    }

    @Transactional(readOnly = true)
    public Page<FarmerCultivationDTO> getAllCultivationsPage(Pageable pageable) {
        return cultivationRepository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<FarmerCultivationDTO> getAllCultivations () {
        return cultivationRepository.findAll().stream()
                .map(mapper::toDto).toList();
    }
    @Transactional
    public FarmerCultivationDTO updateCultivation(Long id, FarmerCultivationDTO dto) {
        FarmerCultivation existing = cultivationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CULTIVATION_NOT_FOUND));

        mapper.toEntity(dto); // Map DTO fields to entity
        existing.setCropType(dto.getCropType());
        existing.setCultivationDate(dto.getCultivationDate());
        existing.setHarvestDate(dto.getHarvestDate());
        existing.setLocation(dto.getLocation());
        existing.setLandSize(dto.getLandSize());
        existing.setExpectedYield(dto.getExpectedYield());
        existing.setMethodOfCultivation(dto.getMethodOfCultivation());
        existing.setDescription(dto.getDescription());
        existing.setPublished(dto.isPublished());

        FarmerCultivation updated = cultivationRepository.save(existing);
        return mapper.toDto(updated);
    }

    @Transactional
    public void deleteCultivation(Long id) {
        if (!cultivationRepository.existsById(id)) {
            throw new BusinessException(BusinessErrorCodes.CULTIVATION_NOT_FOUND);
        }
        cultivationRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByFarmerId(Long farmerId) {
        if (!userRepository.existsById(farmerId)) {
            throw new BusinessException(BusinessErrorCodes.CULTIVATION_NOT_FOUND);
        }
        cultivationRepository.deleteAllByFarmerId(farmerId);
    }
}