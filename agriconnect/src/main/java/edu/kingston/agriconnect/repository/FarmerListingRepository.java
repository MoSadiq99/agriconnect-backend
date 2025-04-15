package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.FarmerListing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FarmerListingRepository extends JpaRepository<FarmerListing, Long> {

    List<FarmerListing> findByCropTypeIgnoreCase(String cropType);

    void deleteAllByFarmerId(Long id);

}
