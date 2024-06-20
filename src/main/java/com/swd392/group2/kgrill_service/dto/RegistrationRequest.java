package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for user registration")
public class RegistrationRequest {

    @Schema(description = "User's first name", example = "Dang Dinh")
    @NotEmpty(message = "First name is mandatory")
    @JsonProperty("first_name")
    private String firstName;

    @Schema(description = "User's last name", example = "Tai")
    @NotEmpty(message = "Last name is mandatory")
    @JsonProperty("last_name")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    @NotEmpty(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User's address", example = "123 Main St, Springfield")
    @NotEmpty(message = "Address cannot be blank")
    private String address;

    @Schema(description = "User's phone number", example = "(+84)877643231")
    @NotEmpty(message = "Phone cannot be blank")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Please enter a valid(+84) phone number")
    private String phone;

    @Schema(description = "User's password", example = "Password1")
    @NotEmpty(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,16}$", message = "Minimum 8 characters, at least one uppercase letter and number")
    private String password;
}
