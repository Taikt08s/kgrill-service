package com.swd392.group2.kgrill_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for location using Google map API")
public class DeliveryLocationDTO {

    @Schema(description = "User's address", example = "123 Main St, Springfield")
    private String address;

    @Schema(description = "Latitude of the address", example = "21.123456")
    private Double latitude;

    @Schema(description = "Longitude of the address", example = "105.123456")
    private Double longitude;

}
