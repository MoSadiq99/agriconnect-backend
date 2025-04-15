package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.LoginResponse;
import edu.kingston.agriconnect.dto.UserLoginDTO;
import edu.kingston.agriconnect.dto.UserRegisterDTO;
import edu.kingston.agriconnect.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterDTO userRegisterDto) {
        if (userRegisterDto == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Request body is missing."));
        }
        authService.registerUser(userRegisterDto);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
            @RequestBody @Valid UserLoginDTO loginRequest) {
        try {
            LoginResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
//            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/activate-account")
    public ResponseEntity<String> activateAccount(
            @RequestParam("token") String token
    ) {
        authService.activateAccount(token);
        return ResponseEntity.ok("Account activated successfully");
    }

}
