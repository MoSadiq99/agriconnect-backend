package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.UserLoginDTO;
import edu.kingston.agriconnect.dto.LoginResponse;
import edu.kingston.agriconnect.dto.UserRegisterDTO;
import edu.kingston.agriconnect.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
//@CrossOrigin(origins = "http://localhost:4200")
//@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO userRegisterDto) {
        if (userRegisterDto == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Request body is missing."));
        }
        authService.registerUser(userRegisterDto);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

//    @PostMapping("/register")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO userRegisterDto) {
//        if (userRegisterDto == null) {
//            return ResponseEntity.badRequest().body("Request body is missing.");
//        }
//        authService.registerUser(userRegisterDto);
////        return ResponseEntity.accepted().build();
////        return ResponseEntity.ok("User registered successfully");
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "User registered successfully");
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserLoginDTO loginRequest) {
        try {
            LoginResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Implement logout logic (e.g., invalidate session or token if necessary)
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping
    public String hello() {
        return "Hello, Agriconnect!";
    }
}
