package com.swd392.group2.kgrill_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery Response object for Revenue")
public class DeliveryResponseForRevenue {

    @JsonProperty(value = "Delivery_order_id", index = 1)
    private Integer id;

    @JsonIgnore
    private UUID accountId;

    @JsonIgnore
    private int shipperId;

    @JsonProperty(value = "Delivery_order_date", index = 5)
    @Schema(description = "Delivery's order date", example = "2024-1-7")
    private Date orderDate;

    @JsonProperty(value = "Delivery_shipped_date", index = 6)
    @Schema(description = "Delivery's shipped date", example = "2021-1-7")
    private Date shippedDate;

    @JsonProperty(value = "Delivery_order_status", index = 4)
    @Schema(description = "Delivery's status", example = "Delivered")
    private String status;

    @JsonProperty(value = "User_name", index = 2)
    @Schema(description = "User's name", example = "Nguyễn Văn A")
    private String userName;

    @JsonProperty(value = "Shipper_name", index = 7)
    @Schema(description = "Shipper's name", example = "Nguyễn Văn B")
    private String shipperName;

    @JsonProperty(value = "Delivery_order_value", index = 8)
    @Schema(description = "Delivery's order value", example = "2000000")
    private float orderValue;

    @JsonProperty(value = "Package_name", index = 3)
    @Schema(description = "Package's name", example = "Combo Bò nướng mĩ vị Tailor")
    private List<String> packageName;








}
