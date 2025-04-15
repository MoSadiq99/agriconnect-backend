package edu.kingston.agriconnect.dto;

import edu.kingston.agriconnect.model.enums.UserStatus;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String address;
    private UserStatus status;
    private List<String> roles; //! Needs to check if this is correct
    private String createdAt;
}