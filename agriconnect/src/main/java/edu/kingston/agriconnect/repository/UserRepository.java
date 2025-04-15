package edu.kingston.agriconnect.repository;

import edu.kingston.agriconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByName(String username);


    @Query("SELECT u FROM User u  join fetch u.roles WHERE u.email = :email")
    User findByEmailEager(@Param("email") String email);
}

