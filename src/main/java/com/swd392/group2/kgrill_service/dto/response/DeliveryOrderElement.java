package com.swd392.group2.kgrill_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DeliveryOrderElement", description = "Delivery Order element that return user ordering for Manager")
public class DeliveryOrderElement {

    @JsonIgnore
    @Schema(name = "id", description = "Delivery Order ID")
    private Integer id;

    @JsonIgnore
    private UUID accountId;

    @Schema(name = "User's ame", description = "Customer Name")
    @JsonProperty("User_name")
    private String userName;

    @Schema(name = "user's Phone", description = "Customer Phone")
    private String Phone;

    @JsonProperty(value = "Package_name")
    @Schema(description = "Package's name", example = "Combo Bò nướng mĩ vị Tailor")
    private List<String> packageName;

    @Schema(name = "orderValue", description = "Total Price")
    @JsonProperty("Order_value")
    private float orderValue;

    @Schema(description = "User's address", example = "123 Main St, Springfield")
    private String address;

    @Schema(description = "Latitude of the address", example = "21.123456")
    private Double latitude;

    @Schema(description = "Longitude of the address", example = "105.123456")
    private Double longitude;

    @Schema(name = "orderDate", description = "Order Date")
    @JsonProperty("Order_date")
    private String orderDate;

    @Schema(name = "orderStatus", description = "Order Status")
    @JsonProperty("Order_status")
    private String orderStatus;


}
