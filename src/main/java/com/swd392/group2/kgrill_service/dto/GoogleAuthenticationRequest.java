package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for social sign in using Google")
public class GoogleAuthenticationRequest {
    @Schema(description = "User's first name", example = "Dang Dinh")
    @NotBlank(message = "First name cannot be blank")
    @JsonProperty("first_name")
    private String firstName;

    @Schema(description = "User's last name", example = "Tai")
    @NotBlank(message = "Last name cannot be blank")
    @JsonProperty("last_name")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Google user unique ID", example = "1022939488858")
    @NotBlank(message = "ID cannot be blank")
    private String id;

    @Schema(description = "Google user photo", example = "http://res.cloudinary.com/torikago/image/upload/vxxx/image.jpg")
    @JsonProperty("photo_url")
    private String photoUrl;
}
