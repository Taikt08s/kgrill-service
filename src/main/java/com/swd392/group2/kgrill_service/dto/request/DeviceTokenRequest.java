package com.swd392.group2.kgrill_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenRequest {

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("device_token")
    private String token;
}
