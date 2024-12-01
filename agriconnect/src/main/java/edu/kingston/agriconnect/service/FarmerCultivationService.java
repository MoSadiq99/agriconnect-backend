package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.model.FarmerCultivation;
import edu.kingston.agriconnect.repository.FarmerCultivationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmerCultivationService {

    private FarmerCultivationRepository cultivationRepository;

    public FarmerCultivation addCultivation(FarmerCultivation cultivation) {
        return cultivationRepository.save(cultivation);
    }

    public List<FarmerCultivation> getCultivationsByFarmer(FarmerCultivation cultivation) {
        Long farmerId = cultivation.getFarmer().getId();
        return cultivationRepository.findByFarmerId(farmerId);
    }
}
