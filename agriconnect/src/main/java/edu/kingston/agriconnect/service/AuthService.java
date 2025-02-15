package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.UserLoginDTO;
import edu.kingston.agriconnect.dto.LoginResponse;
import edu.kingston.agriconnect.dto.UserRegisterDTO;
import edu.kingston.agriconnect.model.*;
import edu.kingston.agriconnect.model.enums.RoleName;
import edu.kingston.agriconnect.model.enums.UserStatus;
import edu.kingston.agriconnect.repository.RoleRepository;
import edu.kingston.agriconnect.repository.TokenRepository;
import edu.kingston.agriconnect.repository.UserRepository;
import edu.kingston.agriconnect.security.JwtService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final Logger log = Logger.getLogger(AuthService.class.getName());
//    private final EmailService emailService;
    @Transactional
    public LoginResponse authenticateUser(UserLoginDTO loginRequest) {

        // Log the incoming request
        System.out.println("Received login request: " + loginRequest.getEmail());
        // Step 1: Check if the user exists
        User user = userRepository.findByEmailEager(loginRequest.getEmail());
        log.info("User: role {} " + user.getRoles());
        // Step 2: Validate password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
//        Optional<Role> roleOpt =  roleRepository.findByRoleNameEager(RoleName.ROLE_FARMER.name());
//        roleOpt.get().getUsers();
        // Step 3: Generate JWT token
//        String token = jwtTokenProvider.generateToken((UserDetails) user);

        String token = "token";
        // Step 4: Return login response with token
        System.out.println("Generated token: " + token);
        System.out.println("User roles: " + user.getRoles());
        return new LoginResponse(token, user.getId(), user.getEmail(), user.getRoles().stream().map(Role::getRoleName).toArray(String[]::new));
    }

    public void registerUser(@Valid UserRegisterDTO dto) {
        // Validate userType
        User user;
        switch (dto.getUserType().toUpperCase()) {
            case "FARMER":
                user = new Farmer();
                break;
            case "BUYER":
                user = new Buyer();
                break;
            case "ADMIN":
                user = new Admin();
                break;
            default:
                throw new IllegalArgumentException("Invalid user type");
        }

        // Set common fields
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        // Set roles
        Set<Role> roles = new HashSet<>();
        for (String roleName : dto.getRoles()) {
            Role role = roleRepository.findByRoleName(RoleName.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        user.setRoles(roles);

        // Save user
        userRepository.save(user);

        // Send validation email
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) {
        var newToken = generateAndSaveActivationToken(user);

        // Send email

    }

    private String generateAndSaveActivationToken(User user) {
        // Generate token
        String generatedToken = generateActivationCode(4);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;

    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}

