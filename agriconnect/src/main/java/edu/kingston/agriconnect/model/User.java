package edu.kingston.agriconnect.model;


import edu.kingston.agriconnect.model.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "users")
public abstract class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull(message = "Name is required")
    private String name;

//    @Email(message = "Invalid email format")
    private String email;

//    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;

//    @NotNull(message = "Password is required")
    private String password;

//    @NotNull(message = "Address is required")
    private String address;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

}
