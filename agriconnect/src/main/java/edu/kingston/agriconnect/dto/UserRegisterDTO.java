package edu.kingston.agriconnect.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserRegisterDTO {

    @NotEmpty(message = "Name is required")
    @NotBlank(message = "Name is required")
    private String name;

    @NotEmpty(message = "Password is required")
    @NotBlank(message = "Password is required")
    private String password;

    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Phone is required")
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

    @NotEmpty(message = "Address is required")
    @NotBlank(message = "Address is required")
    private String address;

    @NotEmpty(message = "User type is required")
    @NotBlank(message = "User type is required")
    private String userType; // e.g., FARMER, BUYER, ADMIN

    @NotEmpty(message = "Roles are required")
    private Set<String> roles; // e.g., ["ROLE_FARMER"], ["ROLE_BUYER"]
}
