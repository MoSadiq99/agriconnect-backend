package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.FarmerCultivationDTO;
import edu.kingston.agriconnect.model.FarmerCultivation;
import edu.kingston.agriconnect.repository.FarmerCultivationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FarmerCultivationService {


    private final FarmerCultivationRepository cultivationRepository;

    @Autowired
    public FarmerCultivationService(FarmerCultivationRepository cultivationRepository) {
        this.cultivationRepository = cultivationRepository;
    }

    public FarmerCultivation addCultivation(FarmerCultivation cultivation) {
        return cultivationRepository.save(cultivation);
    }

    public List<FarmerCultivation> getCultivationsByFarmer(Long farmerId) {
        return cultivationRepository.findByFarmerId(farmerId);
    }

    public List<FarmerCultivation> getAllCultivations() {
        return cultivationRepository.findAll();
    }

    public FarmerCultivation update(Long id, FarmerCultivationDTO dto) {
        FarmerCultivation existingFarmerCultivation = cultivationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer cultivation not found"));

        existingFarmerCultivation.setCropType(dto.getCropType());
        existingFarmerCultivation.setCultivationDate(dto.getCultivationDate());
        existingFarmerCultivation.setHarvestDate(dto.getHarvestDate());
        existingFarmerCultivation.setLocation(dto.getLocation());
        existingFarmerCultivation.setLandSize(dto.getLandSize());
        existingFarmerCultivation.setExpectedYield(dto.getExpectedYield());
        existingFarmerCultivation.setMethodOfCultivation(dto.getMethodOfCultivation());
        existingFarmerCultivation.setDescription(dto.getDescription());

        return cultivationRepository.save(existingFarmerCultivation);
    }
}
