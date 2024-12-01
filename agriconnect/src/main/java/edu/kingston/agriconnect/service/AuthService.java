package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.LoginRequest;
import edu.kingston.agriconnect.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        // Implement user authentication logic here
        // Return the authenticated user's information
        return new LoginResponse("token", 1L, "email");
    }
}
