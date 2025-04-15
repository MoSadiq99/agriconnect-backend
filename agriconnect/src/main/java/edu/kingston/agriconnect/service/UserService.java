package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.UserDTO;
import edu.kingston.agriconnect.model.Role;
import edu.kingston.agriconnect.model.User;
import edu.kingston.agriconnect.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FarmerCultivationService farmerCultivationService;
    private final FarmerListingService farmerListingService;
    private final BuyerRequestService buyerRequestService;

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    public User findByUsername(String username) {
        return userRepository.findByName(username).orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    public User findByEmailEager(String username) {
        return userRepository.findByEmailEager(username);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserDTO dto = new UserDTO();
        mapToUserDto(user, dto);
        return dto;
    }

    private void mapToUserDto(User user, UserDTO dto) {
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPassword(user.getPassword());
        dto.setAddress(user.getAddress());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(String.valueOf(user.getCreatedAt()));
        dto.setRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));
    }

    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ROLE_FARMER"))) {
            farmerListingService.deleteAllByFarmerId(user.getId());
            farmerCultivationService.deleteAllByFarmerId(user.getId());
        }
        if (user.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ROLE_BUYER"))) {
            buyerRequestService.deleteAllByBuyerId(user.getId());
        }
        userRepository.deleteById(id);
    }

    public UserDTO updateUser(Long id, UserDTO userDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setName(userDto.getName());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());

        userRepository.save(user);
        return getUserById(id);
    }


    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserDTO dto = new UserDTO();
            mapToUserDto(user, dto);
            return dto;
        }).collect(Collectors.toList());
    }


    // Todo - For Social login
//    public User findOrCreateUser(String email, String name) {
//        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email)));
//        if (user.isPresent()) {
//            return user.get();
//        } else {
//            return userRepository.save(newUser);
//        }
//    }
}
