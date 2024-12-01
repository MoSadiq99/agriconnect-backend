package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.FarmerCultivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FarmerCultivationRepository extends JpaRepository<FarmerCultivation, Long> {

//    List<FarmerCultivation> findByFarmerId(Long farmerId);

    // Query to find all cultivations for a specific farmer, assuming the farmer is a "FARMER" type
    @Query("SELECT fc FROM FarmerCultivation fc WHERE fc.farmer.id = :farmerId AND TYPE(fc.farmer) = Farmer")
    List<FarmerCultivation> findByFarmerId(@Param("farmerId") Long farmerId);
}
