package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {
}
