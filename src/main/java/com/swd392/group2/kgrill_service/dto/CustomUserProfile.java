package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for account information list")
public class CustomUserProfile {

    @JsonProperty(value = "user_id", index = 1)
    private UUID id;

    @Schema(description = "User's first name", example = "Dang Dinh")
    @JsonProperty(value = "first_name", index = 2)
    private String firstName;

    @Schema(description = "User's last name", example = "Tai")
    @JsonProperty(value = "last_name", index = 3)
    private String lastName;

    @Schema(description = "User's email", example = "elysia112@gmail.com")
    @JsonProperty(value = "email", index = 4)
    private String email;

    @Schema(description = "User's address", example = "123 Main St, Springfield")
    @JsonProperty(value = "address", index = 5)
    private String address;

    @JsonProperty(index = 6)
    @Schema(description = "Latitude of the address", example = "21.123456")
    private Double latitude;

    @JsonProperty(index = 7)
    @Schema(description = "Longitude of the address", example = "105.123456")
    private Double longitude;

    @JsonProperty(index = 8)
    @Schema(description = "User's gender", example = "Male")
    private String gender;

    @JsonProperty(index = 9)
    @Schema(description = "User's phone number", example = "(+84)877643231")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Please enter a valid(+84) phone number")
    private String phone;

    @Schema(description = "User's role", example = "USER")
    @JsonProperty(value = "role", index = 10)
    private String role;

    @Schema(description = "User's status", example = "true")
    @JsonProperty(value = "account_not_locked",index = 11)
    private boolean accountNotLocked;

    @Schema(description = "User's avatar", example = "https://www.google.com")
    @JsonProperty(value = "profile_picture", index = 12)
    private String profilePicture;

    @Schema(description = "User's date of birth", example = "26-10-2003")
    @JsonProperty(value = "dob", index = 13)
    private String dob;


}
