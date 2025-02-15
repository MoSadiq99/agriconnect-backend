package edu.kingston.agriconnect.security;

import edu.kingston.agriconnect.model.User;
import edu.kingston.agriconnect.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    //* Load user by username

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by username
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get the roles and convert them to Spring Security's GrantedAuthority
//        Set<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
//                .collect(Collectors.toSet());

        return new UserDetailsImpl(user);
    }
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByName(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // Get the roles and convert them to Spring Security's GrantedAuthority
//        Set<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
//                .collect(Collectors.toSet());
//
//        // Build UserDetails with Spring Security's User class
//        UserBuilder builder = User.withUsername(user.getUsername());
//        builder.password(user.getPasswordHash()); // Use the hashed password
//        builder.authorities(grantedAuthorities); // Use the roles from the database
//
////        return builder.build();
//        return new UserDetailsImpl(user);
////        return null;
//    }
}
