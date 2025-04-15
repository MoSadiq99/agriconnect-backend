package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.LoginResponse;
import edu.kingston.agriconnect.dto.UserLoginDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final Logger log = Logger.getLogger(AuthService.class.getName());
//    private final EmailService emailService;

    @Value("${spring.application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Transactional
    public LoginResponse authenticateUser(UserLoginDTO loginRequest) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = (User) auth.getPrincipal();
        claims.put("id", user.getId());
        var jwtToken = jwtService.generateToken(claims, user);

        return new LoginResponse(jwtToken, user.getId(), user.getEmail(), user.getRoles().stream().map(Role::getRoleName).toArray(String[]::new));
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
        user.setAccountLocked(false);
        user.setEnabled(false);

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
//        sendValidationEmail(user);
    }

//    private void sendValidationEmail(User user) throws MessagingException {
//        var newToken = generateAndSaveActivationToken(user);
//
//        emailService.sendEmail(
//                user.getEmail(),
//                user.getName(),
//                EmailTemplateName.ACTIVATE_ACCOUNT,
//                activationUrl,
//                newToken,
//                "Activate your account"
//
//        );
//
//    }

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

    // ToDO - Account activation through email
    @Transactional
    public void activateAccount(String token) {
        Token savedToken = tokenRepository.findByToken(token);
        if (savedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        User user = savedToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}

