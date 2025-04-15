package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);
}
