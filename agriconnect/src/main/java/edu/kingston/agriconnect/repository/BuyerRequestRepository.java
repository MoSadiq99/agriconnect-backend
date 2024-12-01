package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.BuyerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerRequestRepository extends JpaRepository<BuyerRequest, Long> {
}
