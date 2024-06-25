package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for user sign in")
public class AuthenticationResponse {
    @Schema(description = "Access Token", example = "xxx.yyy.zzz")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "Refresh Token", example = "xxx.yyy.zzz")
    @JsonProperty("refresh_token")
    private String refreshToken;
}

