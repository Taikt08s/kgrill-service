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
@Schema(description = "Dto for Shipper model")
public class ShipperDto {

    @JsonProperty("shipper_id")
    private Integer shipperId;

    @JsonProperty("shipper_full_name")
    private String shipperName;

    @JsonProperty("shipper_phone_number")
    private String shipperPhone;

    @JsonProperty("shipper_status")
    private String shipperStatus;
}
