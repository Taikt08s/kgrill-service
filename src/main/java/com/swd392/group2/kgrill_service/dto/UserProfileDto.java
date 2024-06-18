package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for account information update")
public class UserProfileDto {

    @JsonIgnore
    private UUID id;

    @Schema(description = "User's first name", example = "Dang Dinh")
    @NotBlank(message = "First name cannot be blank")
    @JsonProperty("first-name")
    private String firstName;

    @Schema(description = "User's last name", example = "Tai")
    @NotBlank(message = "Last name cannot be blank")
    @JsonProperty("last-name")
    private String lastName;

    @Schema(description = "User's address", example = "123 Main St, Springfield")
    @NotBlank(message = "Address cannot be blank")
    private String address;

    @Schema(description = "Latitude of the address", example = "21.123456")
    @NotBlank(message = "Latitude cannot be blank")
    private Double latitude;

    @Schema(description = "Longitude of the address", example = "105.123456")
    @NotBlank(message = "Longitude cannot be blank")
    private Double longitude;

    @Schema(description = "User's gender", example = "Male")
    @NotBlank(message = "Gender cannot be blank")
    private String gender;

    @Schema(description = "User's phone number", example = "(+84)877643231")
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "(84|0[3|5|7|8|9])+([0-9]{8})\\b", message = "Please enter a valid(+84) phone number")
    private String phone;

}
