package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Element object response for Revenue")
public class RevenueElementResponse {

    @JsonProperty(value = "order_date", index = 0)
    private String orderDate;

    @JsonProperty(value = "total_order_number", index = 1)
    private int TotalOrderNumber;

    @JsonProperty(value = "completed_number", index = 2)
    private int completedNumber;

    @JsonProperty(value = "cancelled_number", index = 3)
    private int cancelledNumber;

    @JsonProperty(value = "total_revenue", index = 4)
    private float totalRevenue;

    @JsonProperty(value = "completed_order", index = 5)
    private float completedOrder;

    @JsonProperty(value = "cancelled_order", index = 6)
    private float cancelledOrder;

}
