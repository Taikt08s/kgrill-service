package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for account information list")
public class CustomUserProfile {

    @JsonProperty("user_id")
    private UUID id;

    @Schema(description = "User's first name", example = "Dang Dinh")
    @NotBlank(message = "First name cannot be blank")
    @JsonProperty("first_name")
    private String firstName;

    @Schema(description = "User's last name", example = "Tai")
    @NotBlank(message = "Last name cannot be blank")
    @JsonProperty("last_name")
    private String lastName;

    @Schema(description = "User's email", example = "tinhvv02012003@gmail.com")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Schema(description = "User's address", example = "123 Main St, Springfield")
    @NotBlank(message = "Address cannot be blank")
    private String address;

    @Schema(description = "User's role", example = "USER")
    @NotBlank(message = "Role cannot be blank")
    private String role;

    @Schema(description = "User's status", example = "Active = true, Inactive = false")
    @JsonProperty("account_not_locked")
    private boolean accountNotLocked;



}
