package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.LoginRequest;
import edu.kingston.agriconnect.dto.LoginResponse;
import edu.kingston.agriconnect.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Implement logout logic
        return ResponseEntity.ok("Logged out successfully");
    }
}
