package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.FarmerCultivation;
import edu.kingston.agriconnect.model.FarmerListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerListingRepository extends JpaRepository<FarmerListing, Long> {
}
