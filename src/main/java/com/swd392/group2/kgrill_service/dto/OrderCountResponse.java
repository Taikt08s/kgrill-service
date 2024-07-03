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
@Schema(description = "Response object for Order count")
public class OrderCountResponse {

    @Schema(description = "daily order count", example = "40")
    @JsonProperty(value = "daily_order",index = 1)
    private int dailyOrder;

    @Schema(description = "weekly order count", example = "300")
    @JsonProperty(value = "weekly_order",index = 2)
    private int weeklyOrder;

    @Schema(description = "monthly order count", example = "1200")
    @JsonProperty(value = "monthly_order",index = 3)
    private int monthlyOrder;

    @Schema(description = "yearly order count", example = "15000")
    @JsonProperty(value = "yearly_order",index = 4)
    private int yearlyOrder;

}
